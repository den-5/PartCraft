import { fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type {
    BaseQueryFn,
    FetchArgs,
    FetchBaseQueryError,
} from '@reduxjs/toolkit/query/react';
import { Mutex } from 'async-mutex';
import { logout } from '../auth/authSlice';

const mutex = new Mutex();

const baseQuery = fetchBaseQuery({
    baseUrl: 'http://localhost:8080/api',
    credentials: 'include',
});

export const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    await mutex.waitForUnlock();

    let result = await baseQuery(args, api, extraOptions);

    if (result.error && result.error.status === 401) {
        // Get the request URL
        const url = typeof args === 'string' ? args : args.url;

        // Skip auth check and redirect for public endpoints and optional auth endpoints
        const publicEndpoints = [
            '/auth/login',
            '/auth/sign-up',
            '/auth/refresh',
            '/auth/username-availability',
            '/auth/email-availability',
            '/user/', // Allow checking if user is logged in without redirect
        ];

        // Check if the URL matches any public endpoint (including dynamic paths)
        const isPublicEndpoint = publicEndpoints.some(endpoint =>
            typeof url === 'string' && url.startsWith(endpoint)
        );

        if (isPublicEndpoint) {
            return result;
        }

        if (!mutex.isLocked()) {
            const release = await mutex.acquire();
            try {
                // Try to refresh the token
                const refreshResult = await baseQuery(
                    { url: '/auth/refresh', method: 'GET' },
                    api,
                    extraOptions,
                );

                if (!refreshResult.error) {
                    // Token refresh successful, retry the original request
                    result = await baseQuery(args, api, extraOptions);
                } else {
                    // Token refresh failed, logout and redirect
                    api.dispatch(logout());

                    // Redirect to login page
                    if (typeof window !== 'undefined') {
                        window.location.href = '/login';
                    }
                }
            } finally {
                release();
            }
        } else {
            // Wait for the mutex to be released and retry
            await mutex.waitForUnlock();
            result = await baseQuery(args, api, extraOptions);
        }
    }
    return result;
};

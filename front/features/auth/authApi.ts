import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { UserDto } from './UserDto';

export const authApi = createApi({
    reducerPath: 'authApi',
    baseQuery: fetchBaseQuery({
        baseUrl: process.env.NEXT_PUBLIC_API_URL + '/auth',
    }),
    endpoints: builder => ({
        login: builder.mutation<UserDto, { email: string; password: string }>({
            query: credentials => ({
                url: '/login',
                method: 'POST',
                body: credentials,
            }),
        }),
        signup: builder.mutation<
            UserDto,
            { email: string; password: string; username: string }
        >({
            query: credentials => ({
                url: '/sign-up',
                method: 'POST',
                body: credentials,
            }),
        }),
        refresh: builder.query<void, void>({
            query: () => ({
                url: '/refresh',
                method: 'GET',
                responseHandler: 'text',
            }),
        }),
        logout: builder.mutation<void, void>({
            query: () => ({
                url: '/logout',
                method: 'POST',
                responseHandler: 'text',
            }),
        }),
        usernameAvailability: builder.query<boolean, string>({
            query: username => ({
                url: `/username-availability/${username}`,
                method: 'GET',
            }),
        }),
        emailAvailability: builder.query<boolean, string>({
            query: email => ({
                url: `/email-availability/${email}`,
                method: 'GET',
            }),
        }),
    }),
});

export const {
    useLoginMutation,
    useSignupMutation,
    useRefreshQuery,
    useLogoutMutation,
    useUsernameAvailabilityQuery,
    useEmailAvailabilityQuery,
} = authApi;

import { UserDTO } from '@/shared/types';
import { apiSlice } from '@/features/api/apiSlice';

export const authApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        login: builder.mutation<UserDTO, { email: string; password: string }>({
            query: (credentials: { email: string; password: string }) => ({
                url: '/auth/login',
                method: 'POST',
                body: credentials,
            }),
        }),
        signup: builder.mutation<
            UserDTO,
            { email: string; password: string; username: string }
        >({
            query: (credentials: {
                email: string;
                password: string;
                username: string;
            }) => ({
                url: '/auth/sign-up',
                method: 'POST',
                body: credentials,
            }),
        }),
        refresh: builder.query<void, void>({
            query: (): {
                url: string;
                method: string;
                responseHandler?: 'text';
            } => ({
                url: '/auth/refresh',
                method: 'GET',
                responseHandler: 'text',
            }),
        }),
        logout: builder.mutation<void, void>({
            query: (): {
                url: string;
                method: string;
                responseHandler?: 'text';
            } => ({
                url: '/auth/logout',
                method: 'POST',
                responseHandler: 'text',
            }),
        }),
        usernameAvailability: builder.query<boolean, string>({
            query: (username: string): { url: string; method: string } => ({
                url: `/auth/username-availability/${username}`,
                method: 'GET',
            }),
        }),
        emailAvailability: builder.query<boolean, string>({
            query: (email: string): { url: string; method: string } => ({
                url: `/auth/email-availability/${email}`,
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
    useLazyUsernameAvailabilityQuery,
    useLazyEmailAvailabilityQuery,
} = authApi;

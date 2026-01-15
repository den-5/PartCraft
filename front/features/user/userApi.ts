import { UserDTO, UpdateUserDTO } from '@/shared/types';
import { apiSlice } from '@/features/api/apiSlice';

export const userApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        // Get current authenticated user
        getMe: builder.query<UserDTO, void>({
            query: (): string => '/user/',
            extraOptions: { maxRetries: 0 },
        }),
        // Get user by username
        getUserByUsername: builder.query<UserDTO, string>({
            query: (username: string): string => `/user/${username}`,
        }),
        // Update current user's sensitive data
        updateUser: builder.mutation<UserDTO, UpdateUserDTO>({
            query: (body: UpdateUserDTO) => ({
                url: '/user/update-sensitive/',
                method: 'PUT',
                body,
            }),
        }),
        // Update user's sensitive data by username (Admin only)
        updateUserByUsername: builder.mutation<
            UserDTO,
            { username: string; data: UpdateUserDTO }
        >({
            query: ({
                username,
                data,
            }: {
                username: string;
                data: UpdateUserDTO;
            }) => ({
                url: `/user/update-sensitive/${username}`,
                method: 'PUT',
                body: data,
            }),
        }),
        // Delete current user account
        deleteMe: builder.mutation<void, void>({
            query: (): { url: string; method: string } => ({
                url: '/user/',
                method: 'DELETE',
            }),
        }),
        // Delete user by username (Admin only)
        deleteUserByUsername: builder.mutation<void, string>({
            query: (username: string): { url: string; method: string } => ({
                url: `/user/${username}`,
                method: 'DELETE',
            }),
        }),
        // Get current user's role
        getMyRole: builder.query<string, void>({
            query: (): string => '/user/role/',
        }),
        // Get user role by username
        getUserRoleByUsername: builder.query<string, string>({
            query: (username: string): string => `/user/role/${username}`,
        }),
        // Update user role (Admin only)
        updateUserRole: builder.mutation<
            UserDTO,
            { username: string; role: string }
        >({
            query: ({
                username,
                role,
            }: {
                username: string;
                role: string;
            }) => ({
                url: '/user/role',
                method: 'PUT',
                params: { username, role },
            }),
        }),
    }),
});

export const {
    useGetMeQuery,
    useGetUserByUsernameQuery,
    useUpdateUserMutation,
    useUpdateUserByUsernameMutation,
    useDeleteMeMutation,
    useDeleteUserByUsernameMutation,
    useGetMyRoleQuery,
    useGetUserRoleByUsernameQuery,
    useUpdateUserRoleMutation,
} = userApi;

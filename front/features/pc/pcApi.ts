import { apiSlice } from '@/features/api/apiSlice';
import { CreatePCDTO, PCDTO, UpdatePCDTO } from '@/shared/types';

export const pcApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        getPC: builder.query<PCDTO, number>({
            query: (id: number): string => `/pc/${id}`,
        }),
        createPC: builder.mutation<PCDTO, CreatePCDTO>({
            query: body => ({
                url: '/pc/',
                method: 'POST',
                body,
            }),
        }),
        getUserPCs: builder.query<PCDTO[], string>({
            query: (username: string) => `/pc/user/${username}`,
        }),
        updatePCFields: builder.mutation<
            PCDTO,
            { id: number; data: UpdatePCDTO }
        >({
            query: ({ id, data }) => ({
                url: `/pc/update-fields/${id}`,
                method: 'PUT',
                body: data,
            }),
        }),
        updatePCComponents: builder.mutation<
            PCDTO,
            { id: number; data: UpdatePCDTO }
        >({
            query: ({ id, data }) => ({
                url: `/pc/update-components/${id}`,
                method: 'PUT',
                body: data,
            }),
        }),
        deletePCById: builder.mutation<void, number>({
            query: (id: number) => ({
                url: `/pc/${id}`,
                method: 'DELETE',
            }),
        }),
    }),
});

export const {
    useGetPCQuery,
    useCreatePCMutation,
    useGetUserPCsQuery,
    useUpdatePCFieldsMutation,
    useUpdatePCComponentsMutation,
    useDeletePCByIdMutation,
} = pcApi;

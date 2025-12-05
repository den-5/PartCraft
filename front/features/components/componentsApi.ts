import { apiSlice } from '@/features/api/apiSlice';
import { BaseComponentDTO } from '@/shared/types';

// 1. Define valid components paths based on your backend Controllers
export type ComponentType =
    | 'cpu'
    | 'gpu'
    | 'ram-kit'
    | 'storage'
    | 'psu'
    | 'case'
    | 'cpu-cooler'
    | 'case-cooler'
    | 'motherboard';

export const componentsApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        // GET ALL: /api/{type}
        getComponents: builder.query<
            BaseComponentDTO[],
            { type: ComponentType }
        >({
            query: ({ type }) => `/${type}`,
            providesTags: (result, error, { type }) =>
                result
                    ? [
                          // Tag for the specific list (e.g., 'cpu-LIST')
                          { type: 'Component', id: `${type}-LIST` },
                          // Tags for individual items
                          ...result.map(({ id }) => ({
                              type: 'Component' as const,
                              id: `${type}_${id}`,
                          })),
                      ]
                    : [{ type: 'Component', id: `${type}-LIST` }],
        }),

        // GET ONE: /api/{type}/{id}
        getComponentById: builder.query<
            BaseComponentDTO,
            { type: ComponentType; id: number }
        >({
            query: ({ type, id }) => `/${type}/${id}`,
            providesTags: (result, error, { type, id }) => [
                { type: 'Component', id: `${type}_${id}` },
            ],
        }),

        // CREATE: POST /api/{type}
        createComponent: builder.mutation<
            BaseComponentDTO,
            { type: ComponentType; data: Partial<BaseComponentDTO> }
        >({
            query: ({ type, data }) => ({
                url: `/${type}`,
                method: 'POST',
                body: data,
            }),
            invalidatesTags: (result, error, { type }) => [
                { type: 'Component', id: `${type}-LIST` },
            ],
        }),

        // UPDATE: PUT /api/{type}/{id}
        updateComponent: builder.mutation<
            BaseComponentDTO,
            { type: ComponentType; id: number; data: Partial<BaseComponentDTO> }
        >({
            query: ({ type, id, data }) => ({
                url: `/${type}/${id}`,
                method: 'PUT',
                body: data,
            }),
            invalidatesTags: (result, error, { type, id }) => [
                { type: 'Component', id: `${type}_${id}` },
                { type: 'Component', id: `${type}-LIST` },
            ],
        }),

        // DELETE: DELETE /api/{type}/{id}
        deleteComponent: builder.mutation<
            void,
            { type: ComponentType; id: number }
        >({
            query: ({ type, id }) => ({
                url: `/${type}/${id}`,
                method: 'DELETE',
            }),
            invalidatesTags: (result, error, { type }) => [
                { type: 'Component', id: `${type}-LIST` },
            ],
        }),
    }),
});

export const {
    useGetComponentsQuery,
    useGetComponentByIdQuery,
    useCreateComponentMutation,
    useUpdateComponentMutation,
    useDeleteComponentMutation,
} = componentsApi;

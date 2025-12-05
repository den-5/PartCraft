import { useCallback } from 'react';
import {
    useGetComponentsQuery,
    useGetComponentByIdQuery,
    useCreateComponentMutation,
    useUpdateComponentMutation,
    useDeleteComponentMutation,
    ComponentType,
} from './componentsApi';
import { BaseComponentDTO } from '@/shared/types';

export function useComponentManager<T extends BaseComponentDTO>(
    type: ComponentType,
) {
    const listQuery = useGetComponentsQuery({ type });

    const [createTrigger, createResult] = useCreateComponentMutation();
    const [updateTrigger, updateResult] = useUpdateComponentMutation();
    const [deleteTrigger, deleteResult] = useDeleteComponentMutation();

    const create = useCallback(
        (data: Partial<T>) => {
            return createTrigger({ type, data }).unwrap();
        },
        [createTrigger, type],
    );

    const update = useCallback(
        (id: number, data: Partial<T>) => {
            return updateTrigger({ type, id, data }).unwrap();
        },
        [updateTrigger, type],
    );

    const remove = useCallback(
        (id: number) => {
            return deleteTrigger({ type, id }).unwrap();
        },
        [deleteTrigger, type],
    );

    return {
        // Data
        items: listQuery.data as T[] | undefined,
        isLoading:
            listQuery.isLoading ||
            createResult.isLoading ||
            updateResult.isLoading ||
            deleteResult.isLoading,
        isFetching: listQuery.isFetching,

        // Operations
        create,
        update,
        remove,

        results: {
            create: createResult,
            update: updateResult,
            delete: deleteResult,
        },
    };
}

export function useComponentDetail<T extends BaseComponentDTO>(
    type: ComponentType,
    id: number | undefined,
) {
    const query = useGetComponentByIdQuery(
        { type, id: id! },
        { skip: id === undefined },
    );
    return { ...query, data: query.data as T | undefined };
}

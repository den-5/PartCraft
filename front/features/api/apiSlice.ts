import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from './customBaseQuery';

export const apiSlice = createApi({
    reducerPath: 'api',
    baseQuery: baseQueryWithReauth,
    tagTypes: ['Component', 'User', 'PC'],
    endpoints: () => ({}),
    refetchOnMountOrArgChange: false,
    refetchOnFocus: false,
    refetchOnReconnect: false,
});

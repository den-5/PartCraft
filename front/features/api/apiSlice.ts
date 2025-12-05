import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from './customBaseQuery';

export const apiSlice = createApi({
    reducerPath: 'api',
    baseQuery: baseQueryWithReauth,
    tagTypes: ['Component', 'User', 'PC'], // Add 'Component' here
    endpoints: () => ({}),
});

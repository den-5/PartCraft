import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import { apiSlice } from '@/features/api/apiSlice';

export function makeStore() {
    return configureStore({
        reducer: {
            auth: authReducer,
            [apiSlice.reducerPath]: apiSlice.reducer,
        },
        middleware: getDefaultMiddleware =>
            getDefaultMiddleware().concat(apiSlice.middleware),
    });
}

export type AppStore = ReturnType<typeof makeStore>;
export type RootState = ReturnType<ReturnType<typeof makeStore>['getState']>;
export type AppDispatch = ReturnType<typeof makeStore>['dispatch'];

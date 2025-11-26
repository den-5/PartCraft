import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import { authApi } from '@/features/auth/authApi';

export function makeStore() {
    return configureStore({
        reducer: {
            auth: authReducer,
            [authApi.reducerPath]: authApi.reducer,
        },
        middleware: getDefaultMiddleware =>
            getDefaultMiddleware().concat(authApi.middleware),
    });
}

export type AppStore = ReturnType<typeof makeStore>;
export type RootState = ReturnType<ReturnType<typeof makeStore>['getState']>;
export type AppDispatch = ReturnType<typeof makeStore>['dispatch'];

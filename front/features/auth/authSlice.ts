import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserDTO } from '@/shared/types';

interface AuthState {
    user: UserDTO | null;
    isAuthenticated: boolean;
    isInitialized: boolean;
}

const initialState: AuthState = {
    user: null,
    isAuthenticated: false,
    isInitialized: false,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setCredentials: (state, action: PayloadAction<UserDTO>) => {
            state.user = action.payload;
            state.isAuthenticated = true;
        },
        logout: state => {
            state.user = null;
            state.isAuthenticated = false;
        },
        setAppInitialized: (state, action: PayloadAction<boolean>) => {
            state.isInitialized = action.payload;
        },
    },
});

export const { setCredentials, logout, setAppInitialized } = authSlice.actions;
export default authSlice.reducer;

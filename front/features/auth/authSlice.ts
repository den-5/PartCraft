import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserDto } from './UserDto';
import { authApi } from '@/features/auth/authApi';
export const { useLazyEmailAvailabilityQuery } = authApi;

interface AuthState {
    user: UserDto | null;
    isAuthenticated: boolean;
}

const initialState: AuthState = {
    user: null,
    isAuthenticated: false,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setCredentials: (state, action: PayloadAction<UserDto>) => {
            state.user = action.payload;
            state.isAuthenticated = true;
        },
        logout: state => {
            state.user = null;
            state.isAuthenticated = false;
        },
    },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;

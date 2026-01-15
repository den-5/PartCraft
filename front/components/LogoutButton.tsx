'use client';
import { useDispatch } from 'react-redux';
import { logout } from '@/features/auth/authSlice';
import { useRouter } from 'next/navigation';
import { useLogoutMutation } from '@/features/auth/authApi';
import { apiSlice } from '@/features/api/apiSlice';

export default function LogoutButton() {
    const dispatch = useDispatch();
    const router = useRouter();
    const [serverLogout, { isLoading }] = useLogoutMutation();

    const handleLogout = async () => {
        try {
            await serverLogout().unwrap();
        } catch (e) {
        }
        dispatch(logout());
        dispatch(apiSlice.util.resetApiState());
        router.push('/');
    };

    return (
        <button
            onClick={handleLogout}
            disabled={isLoading}
            className="px-4 py-2 bg-gradient-to-r from-red-600 to-purple-600 hover:from-red-500 hover:to-purple-500 text-white font-medium rounded-lg transition-all duration-300 shadow-lg shadow-red-500/25 disabled:opacity-60"
        >
            {isLoading ? 'Logging out...' : 'Log Out'}
        </button>
    );
}

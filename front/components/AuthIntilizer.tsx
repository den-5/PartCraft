'use client';

import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useGetMeQuery } from '@/features/user/userApi';
import { setCredentials, setAppInitialized } from '@/features/auth/authSlice';
import { RootState } from '@/lib/store';

export default function AuthInitializer({
    children,
}: {
    children: React.ReactNode;
}) {
    const dispatch = useDispatch();
    const isInitialized = useSelector(
        (state: RootState) => state.auth.isInitialized,
    );

    const { data, isSuccess, isError, error, isLoading } = useGetMeQuery();

    useEffect(() => {
        if (isSuccess && data) {
            dispatch(setCredentials(data));
            dispatch(setAppInitialized(true));
        } else if (isError) {
            dispatch(setAppInitialized(true));
        }
    }, [isSuccess, isError, data, dispatch]);
    if (isLoading || !isInitialized) {
        return (
            <div className="flex min-h-screen w-full items-center justify-center bg-gray-900 text-white">
                <div className="flex flex-col items-center gap-4">
                    <div className="h-12 w-12 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
                    <p className="text-lg font-medium animate-pulse">
                        Loading User Session...
                    </p>
                </div>
            </div>
        );
    }

    // 4. Render the actual app once initialized
    return <>{children}</>;
}

'use client';

import { useGetMeQuery } from '@/features/user/userApi';

export default function Home() {
    const { data, isLoading, error } = useGetMeQuery();

    return (
        <main className="p-8">
            <h1 className="text-2xl font-bold mb-4">
                Welcome to the Home Page
            </h1>
            {isLoading && <div>Loading...</div>}
            {error && (
                <div className="text-red-500">Error loading user data</div>
            )}
            {data && (
                <div>
                    <p>Username: {data.username}</p>
                    <p>Email: {data.email}</p>
                </div>
            )}
        </main>
    );
}

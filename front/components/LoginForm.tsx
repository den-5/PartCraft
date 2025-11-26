'use client';
import React, { useState } from 'react';
import { ValidateUserData } from '@/utils/ValidateUserData';
import { useLoginMutation } from '@/features/auth/authApi';
import { useDispatch } from 'react-redux';
import { setCredentials } from '@/features/auth/authSlice';
import { useRouter } from 'next/navigation';
// Assuming you have this helper function defined for TypeScript safety
import { isFetchBaseQueryError } from '@/utils/error-helpers';

export default function LoginForm() {
    const dispatch = useDispatch();
    const router = useRouter();

    const [password, setPassword] = useState<string>('');
    const [passwordError, setPasswordError] = useState<string>('');

    const [email, setEmail] = useState<string>('');
    const [emailError, setEmailError] = useState<string>('');

    // 1. New state for general server errors
    const [generalError, setGeneralError] = useState<string>('');

    const [login, { isLoading }] = useLoginMutation();

    function checkUserData(): boolean {
        let valid = true;
        setPasswordError('');
        setEmailError('');
        setGeneralError(''); // Clear general error on validation/submit

        if (!ValidateUserData.validatePassword(password)) {
            setPasswordError(
                'At least 8 chars, 1 digit, 1 lower, 1 upper, 1 special, no spaces',
            );
            valid = false;
        }
        if (!ValidateUserData.validateEmail(email)) {
            setEmailError(
                'local-part@domain.tld (TLD must be at least 2 characters)',
            );
            valid = false;
        }
        return valid;
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setGeneralError(''); // Clear previous error
        if (!checkUserData()) return;

        const user = await login({ password, email })
            .unwrap()
            .catch(error => {
                // 3. Updated .catch() block to extract and set the server error
                let serverMessage = 'Login failed due to an unknown error.';

                if (isFetchBaseQueryError(error)) {
                    // This block handles 4xx or 5xx responses
                    // Assuming your server error response body is JSON like { message: "Invalid credentials" }
                    const errorData = error.data as { message?: string };
                    serverMessage =
                        errorData.message || `Server Error ${error.status}`;
                } else if (error instanceof Error) {
                    // Fallback for general JavaScript/network errors
                    serverMessage = error.message;
                }

                setGeneralError(serverMessage);
                return undefined; // Return undefined to stop the success logic
            });

        if (user) {
            dispatch(setCredentials(user));
            router.push('/');
        }
    };

    return (
        <form
            onSubmit={handleSubmit}
            className="space-y-6 bg-gray-900 p-8 rounded-lg shadow-lg w-full max-w-md mx-auto"
        >
            <div>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    className="w-full px-4 py-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {emailError && (
                    <div className="text-red-400 text-sm mt-1">
                        {emailError}
                    </div>
                )}
            </div>
            <div>
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    className="w-full px-4 py-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {passwordError && (
                    <div className="text-red-400 text-sm mt-1">
                        {passwordError}
                    </div>
                )}
            </div>
            <button
                type="submit"
                disabled={isLoading}
                className={`w-full py-2 rounded font-semibold transition-colors ${
                    isLoading
                        ? 'bg-gray-600 text-gray-300 cursor-not-allowed'
                        : 'bg-blue-600 hover:bg-blue-700 text-white'
                }`}
            >
                Log In
            </button>
            {/* 4. Display the new general server error state */}
            {generalError && (
                <div className="text-red-400 text-center mt-2">
                    {generalError}
                </div>
            )}
        </form>
    );
}

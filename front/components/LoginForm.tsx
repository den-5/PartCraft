'use client';
import React, { useState } from 'react';
import { ValidateUserData } from '@/utils/ValidateUserData';
import { useLoginMutation } from '@/features/auth/authApi';
import { useDispatch } from 'react-redux';
import { setCredentials } from '@/features/auth/authSlice';
import { useRouter } from 'next/navigation';
import { isFetchBaseQueryError, getErrorMessage } from '@/utils/error-helpers';

export default function LoginForm() {
    const dispatch = useDispatch();
    const router = useRouter();

    const [password, setPassword] = useState<string>('');
    const [passwordError, setPasswordError] = useState<string>('');

    const [email, setEmail] = useState<string>('');
    const [emailError, setEmailError] = useState<string>('');

    const [generalError, setGeneralError] = useState<string>('');

    const [login, { isLoading }] = useLoginMutation();

    const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

    function checkUserData(): boolean {
        let valid = true;
        setPasswordError('');
        setEmailError('');
        setGeneralError('');

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
        setGeneralError('');
        if (!checkUserData()) return;

        try {
            const userData = await login({ email, password }).unwrap();
            dispatch(setCredentials(userData));
            router.push('/');
        } catch (err) {
            if (isFetchBaseQueryError(err)) {
                setGeneralError(getErrorMessage(err));
            } else {
                setGeneralError('An unexpected error occurred.');
            }
        }
    };

    const inputClasses = 'w-full px-4 py-3 bg-gray-800/80 border border-gray-700 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200';

    return (
        <form onSubmit={handleSubmit} className="space-y-5">
            {/* Email Field */}
            <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                    <span className="flex items-center gap-2">
                        <span className="w-2 h-2 bg-blue-400 rounded-full"></span>
                        Email
                    </span>
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                        </svg>
                    </div>
                    <input
                        type="email"
                        placeholder="your@email.com"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        className={`${inputClasses} pl-10`}
                    />
                </div>
                {emailError && (
                    <div className="flex items-center gap-2 mt-2 text-red-400 text-sm">
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        {emailError}
                    </div>
                )}
            </div>

            {/* Password Field */}
            <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                    <span className="flex items-center gap-2">
                        <span className="w-2 h-2 bg-green-400 rounded-full"></span>
                        Password
                    </span>
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                        </svg>
                    </div>
                    <input
                        type="password"
                        placeholder="Enter your password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        className={`${inputClasses} pl-10`}
                    />
                </div>
                {passwordError && (
                    <div className="flex items-center gap-2 mt-2 text-red-400 text-sm">
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        {passwordError}
                    </div>
                )}
            </div>

            {/* Submit Button */}
            <button
                type="submit"
                disabled={isLoading}
                className="w-full py-4 px-6 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 disabled:from-gray-600 disabled:to-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-xl shadow-lg shadow-blue-500/25 hover:shadow-blue-500/40 transition-all duration-300 flex items-center justify-center gap-3"
            >
                {isLoading ? (
                    <>
                        <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                        Signing in...
                    </>
                ) : (
                    <>
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                        </svg>
                        Sign In
                    </>
                )}
            </button>

            {/* General Error */}
            {generalError && (
                <div className="bg-red-900/30 border border-red-500/50 rounded-xl p-4 flex items-center gap-3">
                    <div className="w-8 h-8 bg-red-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                        <svg className="w-5 h-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </div>
                    <p className="text-red-400 font-medium">{generalError}</p>
                </div>
            )}

            {/* Divider */}
            <div className="relative my-6">
                <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-gray-700"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                    <span className="px-4 bg-gray-800/50 text-gray-400">or continue with</span>
                </div>
            </div>

            {/* Google Sign-In Button */}
            <a
                href={`${API_URL}/oauth2/authorization/google`}
                className="w-full py-3 px-6 bg-white hover:bg-gray-100 text-gray-800 font-semibold rounded-xl shadow-lg transition-all duration-300 flex items-center justify-center gap-3"
            >
                <svg className="w-5 h-5" viewBox="0 0 24 24">
                    <path
                        fill="#4285F4"
                        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                    />
                    <path
                        fill="#34A853"
                        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                    />
                    <path
                        fill="#FBBC05"
                        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                    />
                    <path
                        fill="#EA4335"
                        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                    />
                </svg>
                Sign in with Google
            </a>
        </form>
    );
}

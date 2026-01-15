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
        </form>
    );
}

'use client';
import React, {useEffect} from 'react';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {signUpSchema, SignUpFormData} from '@/utils/validationSchemas';
import {
    useSignupMutation,
    useUsernameAvailabilityQuery,
} from '@/features/auth/authApi';
import {useDispatch} from 'react-redux';
import {setCredentials} from '@/features/auth/authSlice';
import {useRouter} from 'next/navigation';
import {getErrorMessage} from '@/utils/error-helpers';

export default function SignUpForm() {
    const dispatch = useDispatch();
    const router = useRouter();
    const [signUp, {isLoading}] = useSignupMutation();
    const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

    const {
        register,
        handleSubmit,
        watch,
        setError,
        clearErrors,
        formState: {errors, isValid},
    } = useForm<SignUpFormData>({
        resolver: zodResolver(signUpSchema),
        mode: 'onBlur',
    });

    const username = watch('username');

    // Check username availability
    const {
        data: isUsernameAvailable,
        isLoading: isUsernameChecking,
        error: usernameCheckingError,
    } = useUsernameAvailabilityQuery(username, {
        skip: !username || username.length < 5 || !/^[a-zA-Z0-9]+$/.test(username),
    });

    // Handle username availability response
    useEffect(() => {
        if (username && username.length >= 5 && /^[a-zA-Z0-9]+$/.test(username)) {
            if (isUsernameAvailable === false) {
                setError('username', {
                    type: 'manual',
                    message: 'Username is already taken',
                });
            } else if (isUsernameAvailable === true) {
                clearErrors('username');
            }
        }
        if (usernameCheckingError && username && username.length >= 5) {
            setError('username', {
                type: 'manual',
                message: 'Error checking username availability',
            });
        }
    }, [isUsernameAvailable, usernameCheckingError, username, setError, clearErrors]);

    const onSubmit = async (data: SignUpFormData) => {
        if (isUsernameAvailable === false) {
            setError('username', {type: 'manual', message: 'Username is already taken'});
            return;
        }

        try {
            const userData = await signUp({
                username: data.username,
                email: data.email,
                password: data.password,
            }).unwrap();
            dispatch(setCredentials(userData));
            router.push('/');
        } catch (err) {
            const msg = getErrorMessage(err);
            setError('root', {message: msg});
        }
    };

    const inputClasses = 'w-full px-4 py-3 bg-gray-800/80 border border-gray-700 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-200';

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            {/* Username Field */}
            <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                    <span className="flex items-center gap-2">
                        <span className="w-2 h-2 bg-purple-400 rounded-full"></span>
                        Username
                    </span>
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg
                            className="w-5 h-5 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                            />
                        </svg>
                    </div>
                    <input
                        type="text"
                        placeholder="Choose a username"
                        {...register('username')}
                        className={`${inputClasses} pl-10`}
                    />
                    {isUsernameChecking && (
                        <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
                            <div className="w-4 h-4 border-2 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
                        </div>
                    )}
                </div>
                {errors.username && (
                    <div className="flex items-center gap-2 mt-2 text-red-400 text-sm">
                        <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                        </svg>
                        {errors.username.message}
                    </div>
                )}
            </div>

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
                        <svg
                            className="w-5 h-5 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                            />
                        </svg>
                    </div>
                    <input
                        type="email"
                        placeholder="your@email.com"
                        {...register('email')}
                        className={`${inputClasses} pl-10`}
                    />
                </div>
                {errors.email && (
                    <div className="flex items-center gap-2 mt-2 text-red-400 text-sm">
                        <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                        </svg>
                        {errors.email.message}
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
                        <svg
                            className="w-5 h-5 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                            />
                        </svg>
                    </div>
                    <input
                        type="password"
                        placeholder="Create a strong password"
                        {...register('password')}
                        className={`${inputClasses} pl-10`}
                    />
                </div>
                {errors.password && (
                    <div className="flex items-center gap-2 mt-2 text-red-400 text-sm">
                        <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                        </svg>
                        {errors.password.message}
                    </div>
                )}
            </div>

            {/* Submit Button */}
            <button
                type="submit"
                disabled={isLoading || !isValid || isUsernameAvailable === false}
                className="w-full py-4 px-6 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 disabled:from-gray-600 disabled:to-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-xl shadow-lg shadow-purple-500/25 hover:shadow-purple-500/40 transition-all duration-300 flex items-center justify-center gap-3"
            >
                {isLoading ? (
                    <>
                        <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                        Creating account...
                    </>
                ) : (
                    <>
                        <svg
                            className="w-5 h-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"
                            />
                        </svg>
                        Create Account
                    </>
                )}
            </button>

            {/* General Error */}
            {errors.root && (
                <div className="bg-red-900/30 border border-red-500/50 rounded-xl p-4 flex items-center gap-3">
                    <div className="w-8 h-8 bg-red-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                        <svg
                            className="w-5 h-5 text-red-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M6 18L18 6M6 6l12 12"
                            />
                        </svg>
                    </div>
                    <p className="text-red-400 font-medium">{errors.root.message}</p>
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

            {/* Google Sign-Up Button */}
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
                Sign up with Google
            </a>
        </form>
    );
}

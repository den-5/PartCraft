'use client';
import React, { useEffect, useState } from 'react';
import { ValidateUserData } from '@/utils/ValidateUserData';
import {
    useEmailAvailabilityQuery,
    useSignupMutation,
    useUsernameAvailabilityQuery,
} from '@/features/auth/authApi';
import { useDispatch } from 'react-redux';
import {
    setCredentials,
    useLazyEmailAvailabilityQuery,
} from '@/features/auth/authSlice';
import { useRouter } from 'next/navigation';
// 1. Import the error helper
import { getErrorMessage } from '@/utils/error-helpers';

export default function SignUpForm() {
    const dispatch = useDispatch();
    const router = useRouter();

    const [username, setUsername] = useState<string>('');
    const [usernameError, setUsernameError] = useState<string>('');

    const [password, setPassword] = useState<string>('');
    const [passwordError, setPasswordError] = useState<string>('');

    const [email, setEmail] = useState<string>('');
    const [emailError, setEmailError] = useState<string>('');

    // 2. Add state for the general server error
    const [generalError, setGeneralError] = useState<string>('');

    const [signUp, { isLoading }] = useSignupMutation();

    const [isDataValid, setIsDataValid] = useState<boolean>(false);

    const {
        data: isUsernameAvailable,
        isLoading: isUsernameChecking,
        error: usernameCheckingError,
    } = useUsernameAvailabilityQuery(username, {
        skip: !ValidateUserData.validateUsername(username),
    });

    useEffect(() => {
        if (ValidateUserData.validateUsername(username)) {
            if (isUsernameAvailable === false) {
                setUsernameError('Username is already taken');
                setIsDataValid(false);
            } else {
                setUsernameError('');
                setIsDataValid(true);
            }
        }
        if (
            usernameCheckingError &&
            ValidateUserData.validateUsername(username)
        ) {
            setUsernameError('Error checking username');
        }
    }, [isUsernameAvailable, usernameCheckingError, username]);

    function checkUserData(): boolean {
        let valid = true;
        // 3. Clear errors before validation
        setUsernameError('');
        setPasswordError('');
        setEmailError('');
        setGeneralError('');

        if (!ValidateUserData.validateUsername(username)) {
            setUsernameError('5-20 alphanumeric characters and numbers only');
            valid = false;
        }
        if (!isUsernameAvailable) {
            setUsernameError('Username is already taken');
            return false;
        }
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
        setGeneralError(''); // Clear previous server errors
        if (!checkUserData()) return;

        const user = await signUp({ username, password, email })
            .unwrap()
            .catch(err => {
                // 4. Extract the error message using the helper and update state
                const msg = getErrorMessage(err);
                setGeneralError(msg);
                return undefined;
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
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    className="w-full px-4 py-2 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {usernameError && (
                    <div className="text-red-400 text-sm mt-1">
                        {usernameError}
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
            <button
                type="submit"
                disabled={isLoading}
                className={`w-full py-2 rounded font-semibold transition-colors ${
                    isLoading || !isDataValid
                        ? 'bg-gray-600 text-gray-300 cursor-not-allowed'
                        : 'bg-blue-600 hover:bg-blue-700 text-white'
                }`}
            >
                Sign Up
            </button>

            {/* 5. Display the specific server error message */}
            {generalError && (
                <div className="text-red-400 text-center mt-2">
                    {generalError}
                </div>
            )}
        </form>
    );
}

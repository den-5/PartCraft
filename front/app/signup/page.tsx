import React from 'react';
import Link from 'next/link';
import SignUpForm from '@/components/SignUpForm';
import Footer from '@/components/Footer';

function Page() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex flex-col">
            <div className="flex-1 flex items-center justify-center py-12 px-4">
                <div className="w-full max-w-md">
                    {/* Header */}
                    <div className="text-center mb-8">
                        <Link href="/" className="inline-flex items-center gap-2 mb-6">
                            <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-blue-500 rounded-xl flex items-center justify-center">
                                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                </svg>
                            </div>
                            <span className="text-2xl font-bold text-white">PartCraft</span>
                        </Link>
                        <h1 className="text-3xl font-bold text-white mb-2">Create Account</h1>
                        <p className="text-gray-400">Join the PC building community</p>
                    </div>

                    {/* Form Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50 shadow-xl">
                        <SignUpForm />
                    </div>

                    {/* Footer Link */}
                    <p className="text-center mt-6 text-gray-400">
                        Already have an account?{' '}
                        <Link href="/login" className="text-purple-400 hover:text-purple-300 font-medium transition-colors">
                            Sign in
                        </Link>
                    </p>
                </div>
            </div>

            {/* Footer */}
            <Footer />
        </div>
    );
}

export default Page;

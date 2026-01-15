'use client';

import { useGetMeQuery } from '@/features/user/userApi';
import Link from 'next/link';
import Footer from '@/components/Footer';
import LogoutButton from '@/components/LogoutButton';

export default function Home() {
    const { data: user, isLoading, error } = useGetMeQuery();

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex flex-col">
            {/* Navigation */}
            <nav className="border-b border-gray-700/50 backdrop-blur-sm bg-gray-900/50 sticky top-0 z-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        {/* Logo */}
                        <Link href="/" className="flex items-center gap-2">
                            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-500 rounded-xl flex items-center justify-center">
                                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                </svg>
                            </div>
                            <span className="text-xl font-bold text-white">PartCraft</span>
                        </Link>

                        {/* Navigation Links */}
                        <div className="flex items-center gap-4">
                            {isLoading ? (
                                <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                            ) : user ? (
                                <>
                                    <Link
                                        href="/builds"
                                        className="px-4 py-2 text-gray-300 hover:text-white font-medium transition-colors hidden sm:block"
                                    >
                                        My Builds
                                    </Link>
                                    <LogoutButton />
                                </>
                            ) : (
                                <>
                                    <Link
                                        href="/login"
                                        className="px-4 py-2 text-gray-300 hover:text-white font-medium transition-colors"
                                    >
                                        Sign In
                                    </Link>
                                    <Link
                                        href="/signup"
                                        className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white font-medium rounded-lg transition-all duration-300 shadow-lg shadow-purple-500/25"
                                    >
                                        Sign Up
                                    </Link>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </nav>

            {/* Hero Section */}
            <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
                <div className="text-center mb-16">
                    <h1 className="text-5xl md:text-6xl font-bold text-white mb-6">
                        Build Your Dream
                        <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent"> PC</span>
                    </h1>
                    <p className="text-xl text-gray-400 max-w-2xl mx-auto mb-10">
                        Design, configure, and share your custom PC builds with our intuitive builder.
                        Choose from hundreds of components and create the perfect machine.
                    </p>

                    {user ? (
                        <Link
                            href="/pc/create"
                            className="inline-flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-semibold text-lg rounded-xl transition-all duration-300 shadow-xl shadow-blue-500/30 hover:shadow-blue-500/50 hover:scale-105"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                            </svg>
                            Create a PC Build
                        </Link>
                    ) : (
                        <div className="flex flex-col sm:flex-row gap-4 justify-center">
                            <Link
                                href="/signup"
                                className="inline-flex items-center justify-center gap-3 px-8 py-4 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white font-semibold text-lg rounded-xl transition-all duration-300 shadow-xl shadow-purple-500/30 hover:shadow-purple-500/50 hover:scale-105"
                            >
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
                                </svg>
                                Get Started Free
                            </Link>
                            <Link
                                href="/login"
                                className="inline-flex items-center justify-center gap-3 px-8 py-4 bg-gray-800/80 hover:bg-gray-700/80 border border-gray-700 text-white font-semibold text-lg rounded-xl transition-all duration-300"
                            >
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                                </svg>
                                Sign In
                            </Link>
                        </div>
                    )}
                </div>

                {/* Features Grid */}
                <div className="grid md:grid-cols-3 gap-8 mt-20">
                    {/* Feature 1 */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50 shadow-xl hover:border-blue-500/50 transition-all duration-300">
                        <div className="w-14 h-14 bg-blue-500/20 rounded-xl flex items-center justify-center mb-6">
                            <svg className="w-7 h-7 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                            </svg>
                        </div>
                        <h3 className="text-xl font-semibold text-white mb-3">Premium Components</h3>
                        <p className="text-gray-400">Choose from CPUs, GPUs, motherboards, RAM, storage, and more from top brands.</p>
                    </div>

                    {/* Feature 2 */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50 shadow-xl hover:border-purple-500/50 transition-all duration-300">
                        <div className="w-14 h-14 bg-purple-500/20 rounded-xl flex items-center justify-center mb-6">
                            <svg className="w-7 h-7 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                            </svg>
                        </div>
                        <h3 className="text-xl font-semibold text-white mb-3">Compatibility Check</h3>
                        <p className="text-gray-400">Our system ensures all your components work together seamlessly.</p>
                    </div>

                    {/* Feature 3 */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50 shadow-xl hover:border-green-500/50 transition-all duration-300">
                        <div className="w-14 h-14 bg-green-500/20 rounded-xl flex items-center justify-center mb-6">
                            <svg className="w-7 h-7 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                            </svg>
                        </div>
                        <h3 className="text-xl font-semibold text-white mb-3">Share Your Build</h3>
                        <p className="text-gray-400">Share your PC builds with the community or keep them private.</p>
                    </div>
                </div>

                {/* User Info Card (if logged in) */}
                {user && (
                    <div className="mt-16 bg-gradient-to-r from-blue-900/30 to-purple-900/30 rounded-2xl p-8 border border-blue-500/30">
                        <div className="flex items-center gap-4 mb-6">
                            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-500 rounded-full flex items-center justify-center">
                                <span className="text-2xl font-bold text-white">
                                    {user.username.charAt(0).toUpperCase()}
                                </span>
                            </div>
                            <div>
                                <h3 className="text-xl font-semibold text-white">{user.username}</h3>
                                <p className="text-gray-400">{user.email}</p>
                            </div>
                        </div>
                        <div className="flex flex-wrap gap-4">
                            <Link
                                href="/pc/create"
                                className="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-medium rounded-xl transition-all duration-300 shadow-lg shadow-blue-500/25"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                                Create New PC
                            </Link>
                            <Link
                                href="/builds"
                                className="inline-flex items-center gap-2 px-6 py-3 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-xl transition-all duration-300"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                                </svg>
                                View My Builds
                            </Link>
                        </div>
                    </div>
                )}

                {/* Error State */}
                {error && !user && (
                    <div className="mt-8 bg-yellow-900/30 border border-yellow-500/50 rounded-xl p-4 flex items-center gap-3 max-w-md mx-auto">
                        <div className="w-8 h-8 bg-yellow-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                            <svg className="w-5 h-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                            </svg>
                        </div>
                        <p className="text-yellow-400">Sign in to access all features</p>
                    </div>
                )}
            </main>

            {/* Footer */}
            <Footer />
        </div>
    );
}

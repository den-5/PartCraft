import React from 'react';
import Link from 'next/link';
import Footer from '@/components/Footer';

export default function CookiesPage() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex flex-col">
            {/* Navigation */}
            <nav className="border-b border-gray-700/50 backdrop-blur-sm bg-gray-900/50 sticky top-0 z-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <Link href="/" className="flex items-center gap-2">
                            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-500 rounded-xl flex items-center justify-center">
                                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                </svg>
                            </div>
                            <span className="text-xl font-bold text-white">PartCraft</span>
                        </Link>
                        <Link href="/" className="text-gray-300 hover:text-white transition-colors">
                            ← Back to Home
                        </Link>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main className="flex-1 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 md:p-12 border border-gray-700/50">
                    <h1 className="text-3xl md:text-4xl font-bold text-white mb-8">Cookie Policy</h1>
                    <p className="text-gray-400 mb-8">Last updated: January 15, 2026</p>

                    <div className="space-y-8 text-gray-300">
                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">What Are Cookies</h2>
                            <p className="leading-relaxed">
                                Cookies are small pieces of text sent to your web browser by a website you visit.
                                A cookie file is stored in your web browser and allows the service or a third-party
                                to recognize you and make your next visit easier and more useful to you.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">How We Use Cookies</h2>
                            <p className="leading-relaxed mb-4">
                                When you use and access PartCraft, we may place cookies on your device.
                                We use cookies for the following purposes:
                            </p>
                            <ul className="list-disc list-inside space-y-2 ml-4">
                                <li>To enable certain functions of the service</li>
                                <li>To provide analytics</li>
                                <li>To store your preferences</li>
                                <li>To enable authentication and keep you logged in</li>
                            </ul>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">Types of Cookies We Use</h2>
                            <div className="space-y-4">
                                <div className="bg-gray-700/30 rounded-lg p-4">
                                    <h3 className="font-semibold text-white mb-2">Essential Cookies</h3>
                                    <p className="text-sm">
                                        These cookies are required for the website to function properly.
                                        They enable basic functions like page navigation and access to secure areas.
                                    </p>
                                </div>
                                <div className="bg-gray-700/30 rounded-lg p-4">
                                    <h3 className="font-semibold text-white mb-2">Analytics Cookies</h3>
                                    <p className="text-sm">
                                        These cookies help us understand how visitors interact with our website
                                        by collecting and reporting information anonymously.
                                    </p>
                                </div>
                                <div className="bg-gray-700/30 rounded-lg p-4">
                                    <h3 className="font-semibold text-white mb-2">Preference Cookies</h3>
                                    <p className="text-sm">
                                        These cookies remember your preferences and settings to enhance your experience.
                                    </p>
                                </div>
                            </div>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">Managing Cookies</h2>
                            <p className="leading-relaxed">
                                Most web browsers allow you to control cookies through their settings preferences.
                                However, if you limit the ability of websites to set cookies, you may worsen your
                                overall user experience. Some features of our service may not function properly
                                if you disable cookies.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">Contact Us</h2>
                            <p className="leading-relaxed">
                                If you have any questions about our Cookie Policy, please contact us at{' '}
                                <a href="mailto:privacy@partcraft.com" className="text-blue-400 hover:text-blue-300">
                                    privacy@partcraft.com
                                </a>
                            </p>
                        </section>
                    </div>
                </div>
            </main>

            {/* Footer */}
            <Footer />
        </div>
    );
}


import React from 'react';
import Link from 'next/link';
import Footer from '@/components/Footer';

export default function TermsPage() {
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
                    <h1 className="text-3xl md:text-4xl font-bold text-white mb-8">Terms of Service</h1>
                    <p className="text-gray-400 mb-8">Last updated: January 15, 2026</p>

                    <div className="space-y-8 text-gray-300">
                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">1. Acceptance of Terms</h2>
                            <p className="leading-relaxed">
                                By accessing and using PartCraft, you accept and agree to be bound by the terms
                                and provision of this agreement. If you do not agree to abide by these terms,
                                please do not use this service.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">2. Use License</h2>
                            <p className="leading-relaxed">
                                Permission is granted to temporarily use PartCraft for personal, non-commercial
                                purposes. This is the grant of a license, not a transfer of title, and under
                                this license you may not modify or copy the materials, use them for any commercial
                                purpose, or attempt to reverse engineer any software contained on the platform.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">3. User Accounts</h2>
                            <p className="leading-relaxed">
                                When you create an account with us, you must provide accurate, complete, and
                                current information. You are responsible for safeguarding the password and for
                                all activities that occur under your account.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">4. PC Build Configurations</h2>
                            <p className="leading-relaxed">
                                While we strive to ensure component compatibility, PartCraft does not guarantee
                                that all PC configurations will work perfectly. Users should verify compatibility
                                with component manufacturers before making purchases.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">5. Disclaimer</h2>
                            <p className="leading-relaxed">
                                The materials on PartCraft are provided on an 'as is' basis. PartCraft makes no
                                warranties, expressed or implied, and hereby disclaims and negates all other
                                warranties including, without limitation, implied warranties or conditions of
                                merchantability, fitness for a particular purpose, or non-infringement.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">6. Limitations</h2>
                            <p className="leading-relaxed">
                                In no event shall PartCraft or its suppliers be liable for any damages arising
                                out of the use or inability to use the materials on the platform, even if
                                PartCraft has been notified of the possibility of such damage.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">7. Contact</h2>
                            <p className="leading-relaxed">
                                Questions about the Terms of Service should be sent to us at{' '}
                                <a href="mailto:legal@partcraft.com" className="text-blue-400 hover:text-blue-300">
                                    legal@partcraft.com
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


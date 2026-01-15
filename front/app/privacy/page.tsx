import React from 'react';
import Link from 'next/link';
import Footer from '@/components/Footer';

export default function PrivacyPage() {
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
                    <h1 className="text-3xl md:text-4xl font-bold text-white mb-8">Privacy Policy</h1>
                    <p className="text-gray-400 mb-8">Last updated: January 15, 2026</p>

                    <div className="space-y-8 text-gray-300">
                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">1. Information We Collect</h2>
                            <p className="leading-relaxed">
                                We collect information you provide directly to us, such as when you create an account,
                                build a PC configuration, or contact us for support. This may include your name, email
                                address, and PC build preferences.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">2. How We Use Your Information</h2>
                            <p className="leading-relaxed">
                                We use the information we collect to provide, maintain, and improve our services,
                                to process your PC builds, to send you technical notices and support messages,
                                and to respond to your comments and questions.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">3. Information Sharing</h2>
                            <p className="leading-relaxed">
                                We do not share your personal information with third parties except as described
                                in this policy. We may share information with vendors, consultants, and other
                                service providers who need access to such information to carry out work on our behalf.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">4. Data Security</h2>
                            <p className="leading-relaxed">
                                We take reasonable measures to help protect information about you from loss, theft,
                                misuse, unauthorized access, disclosure, alteration, and destruction. All data is
                                encrypted in transit and at rest.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">5. Your Rights</h2>
                            <p className="leading-relaxed">
                                You may access, update, or delete your account information at any time by logging
                                into your account settings. You may also contact us to request access to, correction
                                of, or deletion of any personal information.
                            </p>
                        </section>

                        <section>
                            <h2 className="text-xl font-semibold text-white mb-4">6. Contact Us</h2>
                            <p className="leading-relaxed">
                                If you have any questions about this Privacy Policy, please contact us at{' '}
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


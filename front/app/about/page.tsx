import React from 'react';
import Link from 'next/link';
import Footer from '@/components/Footer';

export default function AboutPage() {
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
            <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                {/* Hero */}
                <div className="text-center mb-16">
                    <h1 className="text-4xl md:text-5xl font-bold text-white mb-6">
                        About <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">PartCraft</span>
                    </h1>
                    <p className="text-xl text-gray-400 max-w-3xl mx-auto">
                        We're passionate about helping PC enthusiasts build their dream machines with confidence and ease.
                    </p>
                </div>

                {/* Our Story */}
                <div className="grid md:grid-cols-2 gap-12 mb-16">
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50">
                        <div className="w-14 h-14 bg-blue-500/20 rounded-xl flex items-center justify-center mb-6">
                            <svg className="w-7 h-7 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                        </div>
                        <h2 className="text-2xl font-bold text-white mb-4">Our Story</h2>
                        <p className="text-gray-400 leading-relaxed">
                            Founded in 2024, PartCraft started as a passion project by a group of PC building enthusiasts who were frustrated with the complexity of component compatibility. We believed there had to be a better way to help people build their perfect PC without the headache of researching every single component combination.
                        </p>
                    </div>

                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50">
                        <div className="w-14 h-14 bg-purple-500/20 rounded-xl flex items-center justify-center mb-6">
                            <svg className="w-7 h-7 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                            </svg>
                        </div>
                        <h2 className="text-2xl font-bold text-white mb-4">Our Mission</h2>
                        <p className="text-gray-400 leading-relaxed">
                            Our mission is to democratize PC building by providing an intuitive platform that guides users through every step of the process. Whether you're building your first gaming rig or your tenth workstation, PartCraft ensures you get the perfect combination of components for your needs and budget.
                        </p>
                    </div>
                </div>

                {/* Team Section */}
                <div className="mb-16">
                    <h2 className="text-3xl font-bold text-white text-center mb-12">Meet Our Team</h2>
                    <div className="grid md:grid-cols-3 gap-8">
                        {/* Team Member 1 */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 text-center">
                            <div className="w-24 h-24 bg-gradient-to-br from-blue-500 to-purple-500 rounded-full flex items-center justify-center mx-auto mb-4">
                                <span className="text-3xl font-bold text-white">JD</span>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-1">John Doe</h3>
                            <p className="text-purple-400 text-sm mb-3">CEO & Founder</p>
                            <p className="text-gray-400 text-sm">
                                20+ years in the tech industry with a passion for custom PC builds.
                            </p>
                        </div>

                        {/* Team Member 2 */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 text-center">
                            <div className="w-24 h-24 bg-gradient-to-br from-green-500 to-teal-500 rounded-full flex items-center justify-center mx-auto mb-4">
                                <span className="text-3xl font-bold text-white">JS</span>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-1">Jane Smith</h3>
                            <p className="text-teal-400 text-sm mb-3">CTO</p>
                            <p className="text-gray-400 text-sm">
                                Former Google engineer specializing in hardware compatibility systems.
                            </p>
                        </div>

                        {/* Team Member 3 */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 text-center">
                            <div className="w-24 h-24 bg-gradient-to-br from-orange-500 to-red-500 rounded-full flex items-center justify-center mx-auto mb-4">
                                <span className="text-3xl font-bold text-white">MJ</span>
                            </div>
                            <h3 className="text-xl font-semibold text-white mb-1">Mike Johnson</h3>
                            <p className="text-orange-400 text-sm mb-3">Head of Product</p>
                            <p className="text-gray-400 text-sm">
                                UX expert with 10+ years designing intuitive tech products.
                            </p>
                        </div>
                    </div>
                </div>

                {/* Stats */}
                <div className="bg-gradient-to-r from-blue-900/30 to-purple-900/30 rounded-2xl p-8 border border-blue-500/30">
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
                        <div>
                            <p className="text-4xl font-bold text-white mb-2">50K+</p>
                            <p className="text-gray-400">PCs Built</p>
                        </div>
                        <div>
                            <p className="text-4xl font-bold text-white mb-2">10K+</p>
                            <p className="text-gray-400">Active Users</p>
                        </div>
                        <div>
                            <p className="text-4xl font-bold text-white mb-2">500+</p>
                            <p className="text-gray-400">Components</p>
                        </div>
                        <div>
                            <p className="text-4xl font-bold text-white mb-2">99%</p>
                            <p className="text-gray-400">Satisfaction</p>
                        </div>
                    </div>
                </div>
            </main>

            {/* Footer */}
            <Footer />
        </div>
    );
}


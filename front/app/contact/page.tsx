'use client';
import React, { useState } from 'react';
import Link from 'next/link';
import Footer from '@/components/Footer';

export default function ContactPage() {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        subject: '',
        message: ''
    });
    const [isSubmitted, setIsSubmitted] = useState(false);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // Simulate form submission
        setIsSubmitted(true);
        setTimeout(() => setIsSubmitted(false), 3000);
        setFormData({ name: '', email: '', subject: '', message: '' });
    };

    const inputClasses = 'w-full px-4 py-3 bg-gray-800/80 border border-gray-700 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200';

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
                        Get in <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">Touch</span>
                    </h1>
                    <p className="text-xl text-gray-400 max-w-3xl mx-auto">
                        Have questions? We'd love to hear from you. Send us a message and we'll respond as soon as possible.
                    </p>
                </div>

                <div className="grid md:grid-cols-2 gap-12">
                    {/* Contact Form */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 border border-gray-700/50">
                        <h2 className="text-2xl font-bold text-white mb-6">Send us a message</h2>

                        {isSubmitted && (
                            <div className="mb-6 bg-green-900/30 border border-green-500/50 rounded-xl p-4 flex items-center gap-3">
                                <div className="w-8 h-8 bg-green-500/20 rounded-full flex items-center justify-center">
                                    <svg className="w-5 h-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                    </svg>
                                </div>
                                <p className="text-green-400 font-medium">Message sent successfully!</p>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-5">
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Name</label>
                                <input
                                    type="text"
                                    placeholder="Your name"
                                    value={formData.name}
                                    onChange={e => setFormData({...formData, name: e.target.value})}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Email</label>
                                <input
                                    type="email"
                                    placeholder="your@email.com"
                                    value={formData.email}
                                    onChange={e => setFormData({...formData, email: e.target.value})}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Subject</label>
                                <input
                                    type="text"
                                    placeholder="How can we help?"
                                    value={formData.subject}
                                    onChange={e => setFormData({...formData, subject: e.target.value})}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Message</label>
                                <textarea
                                    placeholder="Your message..."
                                    value={formData.message}
                                    onChange={e => setFormData({...formData, message: e.target.value})}
                                    required
                                    rows={5}
                                    className={`${inputClasses} resize-none`}
                                />
                            </div>
                            <button
                                type="submit"
                                className="w-full py-4 px-6 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-semibold rounded-xl shadow-lg shadow-blue-500/25 hover:shadow-blue-500/40 transition-all duration-300 flex items-center justify-center gap-3"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                                </svg>
                                Send Message
                            </button>
                        </form>
                    </div>

                    {/* Contact Info */}
                    <div className="space-y-8">
                        {/* Office Location */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                            <div className="flex items-start gap-4">
                                <div className="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                                    <svg className="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                                    </svg>
                                </div>
                                <div>
                                    <h3 className="text-lg font-semibold text-white mb-2">Our Office</h3>
                                    <p className="text-gray-400">123 Tech Street</p>
                                    <p className="text-gray-400">Silicon Valley, CA 94000</p>
                                    <p className="text-gray-400">United States</p>
                                </div>
                            </div>
                        </div>

                        {/* Phone */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                            <div className="flex items-start gap-4">
                                <div className="w-12 h-12 bg-green-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                                    <svg className="w-6 h-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                                    </svg>
                                </div>
                                <div>
                                    <h3 className="text-lg font-semibold text-white mb-2">Phone</h3>
                                    <p className="text-gray-400">Main: +1 (555) 123-4567</p>
                                    <p className="text-gray-400">Support: +1 (555) 987-6543</p>
                                    <p className="text-gray-400 text-sm mt-2">Mon-Fri, 9am-6pm PST</p>
                                </div>
                            </div>
                        </div>

                        {/* Email */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                            <div className="flex items-start gap-4">
                                <div className="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                                    <svg className="w-6 h-6 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                    </svg>
                                </div>
                                <div>
                                    <h3 className="text-lg font-semibold text-white mb-2">Email</h3>
                                    <p className="text-gray-400">General: info@partcraft.com</p>
                                    <p className="text-gray-400">Support: support@partcraft.com</p>
                                    <p className="text-gray-400">Sales: sales@partcraft.com</p>
                                </div>
                            </div>
                        </div>

                        {/* Business Hours */}
                        <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                            <div className="flex items-start gap-4">
                                <div className="w-12 h-12 bg-orange-500/20 rounded-xl flex items-center justify-center flex-shrink-0">
                                    <svg className="w-6 h-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                </div>
                                <div>
                                    <h3 className="text-lg font-semibold text-white mb-2">Business Hours</h3>
                                    <p className="text-gray-400">Monday - Friday: 9:00 AM - 6:00 PM</p>
                                    <p className="text-gray-400">Saturday: 10:00 AM - 4:00 PM</p>
                                    <p className="text-gray-400">Sunday: Closed</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>

            {/* Footer */}
            <Footer />
        </div>
    );
}


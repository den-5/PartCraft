'use client';
import React, { useState } from 'react';
import Link from 'next/link';
import { useGetUserPCsQuery, useDeletePCByIdMutation } from '@/features/pc/pcApi';
import { useGetMeQuery } from '@/features/user/userApi';
import Footer from '@/components/Footer';
import { PCDTO } from '@/shared/types';

export default function BuildsPage() {
    const { data: user, isLoading: userLoading } = useGetMeQuery();
    const { data: builds, isLoading: buildsLoading, refetch } = useGetUserPCsQuery(
        user?.username ?? '',
        { skip: !user?.username }
    );
    const [deletePC, { isLoading: isDeleting }] = useDeletePCByIdMutation();
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedBuild, setSelectedBuild] = useState<PCDTO | null>(null);

    const handleDeleteClick = (build: PCDTO) => {
        setSelectedBuild(build);
        setShowDeleteModal(true);
    };

    const handleConfirmDelete = async () => {
        if (!selectedBuild) return;
        setDeletingId(selectedBuild.id);
        try {
            await deletePC(selectedBuild.id).unwrap();
            refetch();
        } catch (error) {
            console.error('Failed to delete PC:', error);
        } finally {
            setDeletingId(null);
            setShowDeleteModal(false);
            setSelectedBuild(null);
        }
    };

    const isLoading = userLoading || buildsLoading;

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
                        <div className="flex items-center gap-4">
                            <Link href="/" className="text-gray-300 hover:text-white transition-colors">
                                Home
                            </Link>
                            <Link
                                href="/pc/create"
                                className="px-4 py-2 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-medium rounded-lg transition-all duration-300 shadow-lg shadow-blue-500/25"
                            >
                                Create New PC
                            </Link>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                {/* Header */}
                <div className="mb-10">
                    <h1 className="text-4xl font-bold text-white mb-3">
                        My <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">Builds</span>
                    </h1>
                    <p className="text-gray-400 text-lg">
                        Manage and view all your PC configurations
                    </p>
                </div>

                {/* Loading State */}
                {isLoading && (
                    <div className="flex items-center justify-center py-20">
                        <div className="flex flex-col items-center gap-4">
                            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                            <p className="text-gray-400">Loading your builds...</p>
                        </div>
                    </div>
                )}

                {/* Not Logged In */}
                {!userLoading && !user && (
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-12 border border-gray-700/50 text-center">
                        <div className="w-20 h-20 bg-yellow-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-10 h-10 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                            </svg>
                        </div>
                        <h2 className="text-2xl font-bold text-white mb-4">Sign In Required</h2>
                        <p className="text-gray-400 mb-8 max-w-md mx-auto">
                            Please sign in to view and manage your PC builds.
                        </p>
                        <div className="flex gap-4 justify-center">
                            <Link
                                href="/login"
                                className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-medium rounded-xl transition-all duration-300"
                            >
                                Sign In
                            </Link>
                            <Link
                                href="/signup"
                                className="px-6 py-3 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-xl transition-all duration-300"
                            >
                                Create Account
                            </Link>
                        </div>
                    </div>
                )}

                {/* No Builds */}
                {!isLoading && user && builds && builds.length === 0 && (
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-12 border border-gray-700/50 text-center">
                        <div className="w-20 h-20 bg-blue-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-10 h-10 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                            </svg>
                        </div>
                        <h2 className="text-2xl font-bold text-white mb-4">No Builds Yet</h2>
                        <p className="text-gray-400 mb-8 max-w-md mx-auto">
                            You haven't created any PC builds yet. Start building your dream machine now!
                        </p>
                        <Link
                            href="/pc/create"
                            className="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white font-medium rounded-xl transition-all duration-300"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                            </svg>
                            Create Your First Build
                        </Link>
                    </div>
                )}

                {/* Builds Grid */}
                {!isLoading && user && builds && builds.length > 0 && (
                    <>
                        <div className="flex items-center justify-between mb-6">
                            <p className="text-gray-400">
                                {builds.length} build{builds.length !== 1 ? 's' : ''} found
                            </p>
                        </div>
                        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {builds.map((build) => (
                                <div
                                    key={build.id}
                                    className="bg-gray-800/50 backdrop-blur-sm rounded-2xl border border-gray-700/50 overflow-hidden hover:border-blue-500/50 transition-all duration-300 group"
                                >
                                    {/* Build Header */}
                                    <div className="p-6 border-b border-gray-700/50">
                                        <div className="flex items-start justify-between mb-3">
                                            <h3 className="text-xl font-semibold text-white group-hover:text-blue-400 transition-colors">
                                                {build.name}
                                            </h3>
                                            <span className={`px-2 py-1 rounded-lg text-xs font-medium ${
                                                build.visibility === 'PUBLIC' 
                                                    ? 'bg-green-500/20 text-green-400'
                                                    : build.visibility === 'PRIVATE'
                                                    ? 'bg-red-500/20 text-red-400'
                                                    : 'bg-yellow-500/20 text-yellow-400'
                                            }`}>
                                                {build.visibility}
                                            </span>
                                        </div>
                                        <p className="text-gray-400 text-sm line-clamp-2">
                                            {build.description || 'No description'}
                                        </p>
                                    </div>

                                    {/* Build Specs */}
                                    <div className="p-6 space-y-3">
                                        {/* CPU */}
                                        {build.cpu && (
                                            <div className="flex items-center gap-3">
                                                <div className="w-8 h-8 bg-red-500/20 rounded-lg flex items-center justify-center">
                                                    <svg className="w-4 h-4 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                                    </svg>
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm text-white truncate">{build.cpu.cpuBrand} {build.cpu.cpuModel}</p>
                                                    <p className="text-xs text-gray-500">{build.cpu.cpuCores} Cores</p>
                                                </div>
                                            </div>
                                        )}

                                        {/* GPU */}
                                        {build.gpu && (
                                            <div className="flex items-center gap-3">
                                                <div className="w-8 h-8 bg-green-500/20 rounded-lg flex items-center justify-center">
                                                    <svg className="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7" />
                                                    </svg>
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm text-white truncate">{build.gpu.gpuBrand} {build.gpu.gpuModel}</p>
                                                    <p className="text-xs text-gray-500">{build.gpu.gpuMemoryGb}GB VRAM</p>
                                                </div>
                                            </div>
                                        )}

                                        {/* RAM */}
                                        {build.ramKit && (
                                            <div className="flex items-center gap-3">
                                                <div className="w-8 h-8 bg-blue-500/20 rounded-lg flex items-center justify-center">
                                                    <svg className="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2" />
                                                    </svg>
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm text-white truncate">{build.ramKit.ramType} {build.ramKit.ramSizeGb}GB</p>
                                                    <p className="text-xs text-gray-500">{build.ramKit.ramSpeedMhz}MHz</p>
                                                </div>
                                            </div>
                                        )}
                                    </div>

                                    {/* Build Stats */}
                                    <div className="px-6 py-4 bg-gray-900/50 border-t border-gray-700/50">
                                        <div className="flex items-center justify-between text-sm">
                                            <div className="flex items-center gap-4">
                                                {build.totalPowerDrawW && (
                                                    <span className="text-gray-400">
                                                        <span className="text-orange-400">{build.totalPowerDrawW}W</span>
                                                    </span>
                                                )}
                                                {build.purpose && (
                                                    <span className="text-gray-500">{build.purpose}</span>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    {/* Actions */}
                                    <div className="p-4 border-t border-gray-700/50 flex gap-2">
                                        <Link
                                            href={`/builds/${build.id}`}
                                            className="flex-1 px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white text-center text-sm font-medium rounded-lg transition-colors"
                                        >
                                            View Details
                                        </Link>
                                        <button
                                            onClick={() => handleDeleteClick(build)}
                                            disabled={isDeleting && deletingId === build.id}
                                            className="px-4 py-2 bg-red-500/20 hover:bg-red-500/30 text-red-400 text-sm font-medium rounded-lg transition-colors disabled:opacity-50"
                                        >
                                            {isDeleting && deletingId === build.id ? (
                                                <div className="w-4 h-4 border-2 border-red-400 border-t-transparent rounded-full animate-spin"></div>
                                            ) : (
                                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                            )}
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                )}
            </main>

            {/* Delete Confirmation Modal */}
            {showDeleteModal && selectedBuild && (
                <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4">
                    <div className="bg-gray-800 rounded-2xl p-6 max-w-md w-full border border-gray-700">
                        <div className="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-8 h-8 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </div>
                        <h3 className="text-xl font-bold text-white text-center mb-2">Delete Build?</h3>
                        <p className="text-gray-400 text-center mb-6">
                            Are you sure you want to delete "<span className="text-white">{selectedBuild.name}</span>"? This action cannot be undone.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="flex-1 px-4 py-3 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-xl transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleConfirmDelete}
                                disabled={isDeleting}
                                className="flex-1 px-4 py-3 bg-red-600 hover:bg-red-500 text-white font-medium rounded-xl transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                            >
                                {isDeleting ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                        Deleting...
                                    </>
                                ) : (
                                    'Delete Build'
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Footer */}
            <Footer />
        </div>
    );
}


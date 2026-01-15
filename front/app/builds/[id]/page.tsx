'use client';
import React, { useState } from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useGetPCQuery, useDeletePCByIdMutation } from '@/features/pc/pcApi';
import { useGetMeQuery } from '@/features/user/userApi';
import Footer from '@/components/Footer';

export default function BuildDetailsPage() {
    const params = useParams();
    const router = useRouter();
    const buildId = Number(params.id);

    const { data: user } = useGetMeQuery();
    const { data: build, isLoading, error } = useGetPCQuery(buildId, { skip: !buildId });
    const [deletePC, { isLoading: isDeleting }] = useDeletePCByIdMutation();
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    const handleDelete = async () => {
        try {
            await deletePC(buildId).unwrap();
            router.push('/builds');
        } catch (error) {
            console.error('Failed to delete:', error);
        }
    };

    const isOwner = user && build && user.id === build.ownerId;

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
                        <Link href="/builds" className="text-gray-300 hover:text-white transition-colors">
                            ← Back to Builds
                        </Link>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                {/* Loading State */}
                {isLoading && (
                    <div className="flex items-center justify-center py-20">
                        <div className="flex flex-col items-center gap-4">
                            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                            <p className="text-gray-400">Loading build details...</p>
                        </div>
                    </div>
                )}

                {/* Error State */}
                {error && (
                    <div className="bg-red-900/30 border border-red-500/50 rounded-2xl p-8 text-center">
                        <div className="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center mx-auto mb-4">
                            <svg className="w-8 h-8 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        </div>
                        <h2 className="text-xl font-bold text-white mb-2">Build Not Found</h2>
                        <p className="text-gray-400 mb-6">The build you're looking for doesn't exist or has been deleted.</p>
                        <Link href="/builds" className="text-blue-400 hover:text-blue-300">
                            Go back to builds
                        </Link>
                    </div>
                )}

                {/* Build Details */}
                {build && (
                    <>
                        {/* Header */}
                        <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-6 mb-8">
                            <div>
                                <div className="flex items-center gap-3 mb-2">
                                    <h1 className="text-3xl md:text-4xl font-bold text-white">{build.name}</h1>
                                    <span className={`px-3 py-1 rounded-lg text-sm font-medium ${
                                        build.visibility === 'PUBLIC'
                                            ? 'bg-green-500/20 text-green-400'
                                            : build.visibility === 'PRIVATE'
                                            ? 'bg-red-500/20 text-red-400'
                                            : 'bg-yellow-500/20 text-yellow-400'
                                    }`}>
                                        {build.visibility}
                                    </span>
                                </div>
                                <p className="text-gray-400 text-lg">{build.description}</p>
                                {build.purpose && (
                                    <p className="text-purple-400 mt-2">Purpose: {build.purpose}</p>
                                )}
                            </div>
                            {isOwner && (
                                <div className="flex gap-3">
                                    <Link
                                        href={`/builds/${build.id}/edit`}
                                        className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white font-medium rounded-lg transition-colors"
                                    >
                                        Edit Build
                                    </Link>
                                    <button
                                        onClick={() => setShowDeleteModal(true)}
                                        className="px-4 py-2 bg-red-500/20 hover:bg-red-500/30 text-red-400 font-medium rounded-lg transition-colors"
                                    >
                                        Delete
                                    </button>
                                </div>
                            )}
                        </div>

                        {/* Stats Bar */}
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                            {build.totalPowerDrawW && (
                                <div className="bg-gray-800/50 rounded-xl p-4 border border-gray-700/50">
                                    <p className="text-gray-400 text-sm">Power Draw</p>
                                    <p className="text-2xl font-bold text-orange-400">{build.totalPowerDrawW}W</p>
                                </div>
                            )}
                            {build.benchmarkScore && (
                                <div className="bg-gray-800/50 rounded-xl p-4 border border-gray-700/50">
                                    <p className="text-gray-400 text-sm">Benchmark</p>
                                    <p className="text-2xl font-bold text-green-400">{build.benchmarkScore}</p>
                                </div>
                            )}
                            {build.estimatedValueUsd && (
                                <div className="bg-gray-800/50 rounded-xl p-4 border border-gray-700/50">
                                    <p className="text-gray-400 text-sm">Est. Value</p>
                                    <p className="text-2xl font-bold text-blue-400">${build.estimatedValueUsd}</p>
                                </div>
                            )}
                            {build.location && (
                                <div className="bg-gray-800/50 rounded-xl p-4 border border-gray-700/50">
                                    <p className="text-gray-400 text-sm">Location</p>
                                    <p className="text-lg font-medium text-white truncate">{build.location}</p>
                                </div>
                            )}
                        </div>

                        {/* Components Grid */}
                        <div className="grid md:grid-cols-2 gap-6">
                            {/* CPU */}
                            {build.cpu && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-red-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">CPU</p>
                                            <h3 className="text-lg font-semibold text-white">{build.cpu.cpuBrand} {build.cpu.cpuModel}</h3>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Cores</p>
                                            <p className="text-white font-medium">{build.cpu.cpuCores}</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Threads</p>
                                            <p className="text-white font-medium">{build.cpu.cpuThreads}</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Base Clock</p>
                                            <p className="text-white font-medium">{build.cpu.cpuBaseClockGhz} GHz</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Boost Clock</p>
                                            <p className="text-white font-medium">{build.cpu.cpuBoostClockGhz} GHz</p>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* GPU */}
                            {build.gpu && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-green-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">GPU</p>
                                            <h3 className="text-lg font-semibold text-white">{build.gpu.gpuBrand} {build.gpu.gpuModel}</h3>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">VRAM</p>
                                            <p className="text-white font-medium">{build.gpu.gpuMemoryGb} GB</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Power Draw</p>
                                            <p className="text-white font-medium">{build.gpu.powerDraw}W</p>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* RAM */}
                            {build.ramKit && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">RAM</p>
                                            <h3 className="text-lg font-semibold text-white">{build.ramKit.ramType} {build.ramKit.ramSizeGb}GB</h3>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Speed</p>
                                            <p className="text-white font-medium">{build.ramKit.ramSpeedMhz} MHz</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Sticks</p>
                                            <p className="text-white font-medium">{build.ramKit.ramSticksCount}</p>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* Storage */}
                            {build.storage && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-cyan-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">Storage</p>
                                            <h3 className="text-lg font-semibold text-white">{build.storage.storageType} {build.storage.storageTotalGb}GB</h3>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* PSU */}
                            {build.psu && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-orange-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">Power Supply</p>
                                            <h3 className="text-lg font-semibold text-white">{build.psu.psuModel}</h3>
                                        </div>
                                    </div>
                                    <div className="bg-gray-900/50 rounded-lg p-3">
                                        <p className="text-xs text-gray-500">Wattage</p>
                                        <p className="text-white font-medium">{build.psu.psuWattage}W</p>
                                    </div>
                                </div>
                            )}

                            {/* Motherboard */}
                            {build.motherboard && (
                                <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                                            <svg className="w-6 h-6 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6z" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-400">Motherboard</p>
                                            <h3 className="text-lg font-semibold text-white">{build.motherboard.motherboardBrand} {build.motherboard.motherboardModel}</h3>
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Socket</p>
                                            <p className="text-white font-medium">{build.motherboard.socketType}</p>
                                        </div>
                                        <div className="bg-gray-900/50 rounded-lg p-3">
                                            <p className="text-xs text-gray-500">Memory Type</p>
                                            <p className="text-white font-medium">{build.motherboard.memoryType}</p>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Timestamps */}
                        <div className="mt-8 pt-8 border-t border-gray-700/50">
                            <div className="flex flex-wrap gap-6 text-sm text-gray-500">
                                {build.createdAt && (
                                    <p>Created: {new Date(build.createdAt).toLocaleDateString()}</p>
                                )}
                                {build.updatedAt && (
                                    <p>Updated: {new Date(build.updatedAt).toLocaleDateString()}</p>
                                )}
                            </div>
                        </div>
                    </>
                )}
            </main>

            {/* Delete Confirmation Modal */}
            {showDeleteModal && (
                <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4">
                    <div className="bg-gray-800 rounded-2xl p-6 max-w-md w-full border border-gray-700">
                        <div className="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-8 h-8 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </div>
                        <h3 className="text-xl font-bold text-white text-center mb-2">Delete Build?</h3>
                        <p className="text-gray-400 text-center mb-6">
                            Are you sure you want to delete this build? This action cannot be undone.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="flex-1 px-4 py-3 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-xl transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
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


import React from 'react';
import CreatePc from '@/components/CreatePC';
import Link from 'next/link';

export default function Page() {
    return (
        <div>
            {/* Home navigation button */}
            <div className="fixed top-4 left-4 z-50">
                <Link href="/">
                    <button className="group flex items-center gap-2 px-5 py-2.5
            bg-white/10 dark:bg-black/20
            hover:bg-white/20 dark:hover:bg-black/30
            backdrop-blur-xl saturate-150
            border border-white/20
            text-gray-900 dark:text-white
            font-medium rounded-full
            transition-all duration-300 ease-[cubic-bezier(0.25,0.1,0.25,1)]
            shadow-[0_8px_32px_0_rgba(31,38,135,0.07)]
            hover:shadow-[0_8px_32px_0_rgba(31,38,135,0.15)] hover:scale-105 active:scale-95">

                        <svg className="w-5 h-5 opacity-70 group-hover:opacity-100 transition-opacity duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                        </svg>
                        <span className="tracking-wide">Home</span>
                    </button>
                </Link>
            </div>
            <CreatePc />
        </div>
    );
}



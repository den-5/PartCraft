'use client';
import React, { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';

export interface MultiSelectOption {
    id: number;
    label: string;
    sublabel?: string;
    specs?: { label: string; value: string }[];
}

interface MultiComponentSelectProps {
    label: string;
    placeholder: string;
    options: MultiSelectOption[];
    value: number[];
    onChange: (value: number[]) => void;
    accentColor?: string;
    icon?: React.ReactNode;
    maxDisplay?: number;
}

const MultiComponentSelect: React.FC<MultiComponentSelectProps> = ({
    label,
    placeholder,
    options,
    value,
    onChange,
    accentColor = 'teal',
    icon,
    maxDisplay = 4,
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [search, setSearch] = useState('');
    const [dropdownPosition, setDropdownPosition] = useState({ top: 0, left: 0, width: 0 });
    const [isMounted, setIsMounted] = useState(false);
    const [openDirection, setOpenDirection] = useState<'down' | 'up'>('down');
    const containerRef = useRef<HTMLDivElement>(null);
    const buttonRef = useRef<HTMLDivElement>(null);
    const searchInputRef = useRef<HTMLInputElement>(null);
    const dropdownRef = useRef<HTMLDivElement>(null);

    const selectedOptions = options.filter(opt => value.includes(opt.id));

    const filteredOptions = options.filter(
        opt =>
            opt.label.toLowerCase().includes(search.toLowerCase()) ||
            opt.sublabel?.toLowerCase().includes(search.toLowerCase())
    );

    // Close dropdown when clicking outside
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as Node;

            // Check if click is inside the container
            if (containerRef.current && containerRef.current.contains(target)) {
                return;
            }

            // Check if click is inside the portal dropdown
            if (dropdownRef.current && dropdownRef.current.contains(target)) {
                return;
            }

            setIsOpen(false);
            setSearch('');
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // Focus search input when dropdown opens
    useEffect(() => {
        if (isOpen && searchInputRef.current) {
            searchInputRef.current.focus();
        }
    }, [isOpen]);

    // Handle portal mounting (client-side only)
    useEffect(() => {
        setIsMounted(true);
    }, []);

    // Function to calculate and update dropdown position
    const updatePosition = () => {
        if (buttonRef.current) {
            const rect = buttonRef.current.getBoundingClientRect();
            const dropdownHeight = 380; // Approximate max height of dropdown
            const spaceBelow = window.innerHeight - rect.bottom;
            const spaceAbove = rect.top;

            // Determine if we should open upward
            const shouldOpenUp = spaceBelow < dropdownHeight && spaceAbove > spaceBelow;

            setOpenDirection(shouldOpenUp ? 'up' : 'down');
            setDropdownPosition({
                top: shouldOpenUp ? rect.top - 8 : rect.bottom + 8,
                left: rect.left,
                width: rect.width,
            });
        }
    };

    // Update dropdown position when opened and on scroll/resize
    useEffect(() => {
        if (isOpen) {
            updatePosition();

            window.addEventListener('scroll', updatePosition, true);
            window.addEventListener('resize', updatePosition);

            return () => {
                window.removeEventListener('scroll', updatePosition, true);
                window.removeEventListener('resize', updatePosition);
            };
        }
    }, [isOpen]);

    const toggleOption = (id: number) => {
        if (value.includes(id)) {
            onChange(value.filter(v => v !== id));
        } else {
            onChange([...value, id]);
        }
    };

    const removeOption = (id: number, e: React.MouseEvent) => {
        e.stopPropagation();
        onChange(value.filter(v => v !== id));
    };

    const clearAll = (e: React.MouseEvent) => {
        e.stopPropagation();
        onChange([]);
    };

    const accentClasses: Record<string, { bg: string; border: string; ring: string; dot: string; hover: string; gradient: string; chip: string; chipHover: string }> = {
        teal: {
            bg: 'bg-teal-500/20',
            border: 'border-teal-500/50',
            ring: 'ring-teal-500',
            dot: 'bg-teal-400',
            hover: 'hover:bg-teal-500/10',
            gradient: 'from-teal-500/20 to-transparent',
            chip: 'bg-teal-500/30 border-teal-500/50 text-teal-200',
            chipHover: 'hover:bg-teal-500/50',
        },
        cyan: {
            bg: 'bg-cyan-500/20',
            border: 'border-cyan-500/50',
            ring: 'ring-cyan-500',
            dot: 'bg-cyan-400',
            hover: 'hover:bg-cyan-500/10',
            gradient: 'from-cyan-500/20 to-transparent',
            chip: 'bg-cyan-500/30 border-cyan-500/50 text-cyan-200',
            chipHover: 'hover:bg-cyan-500/50',
        },
        purple: {
            bg: 'bg-purple-500/20',
            border: 'border-purple-500/50',
            ring: 'ring-purple-500',
            dot: 'bg-purple-400',
            hover: 'hover:bg-purple-500/10',
            gradient: 'from-purple-500/20 to-transparent',
            chip: 'bg-purple-500/30 border-purple-500/50 text-purple-200',
            chipHover: 'hover:bg-purple-500/50',
        },
    };

    const colors = accentClasses[accentColor] || accentClasses.teal;

    return (
        <div className="relative" ref={containerRef}>
            {/* Label */}
            <label className="block text-sm font-medium text-gray-300 mb-2">
                <span className="flex items-center gap-2">
                    <span className={`w-2 h-2 ${colors.dot} rounded-full`}></span>
                    {label}
                </span>
            </label>

            {/* Trigger Button */}
            <div
                ref={buttonRef}
                onClick={() => setIsOpen(!isOpen)}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        setIsOpen(!isOpen);
                    }
                }}
                className={`
                    w-full px-4 py-3 bg-gray-800/80 border rounded-xl text-left
                    transition-all duration-300 group h-[140px] overflow-hidden cursor-pointer
                    ${isOpen 
                        ? `${colors.border} ring-2 ${colors.ring} shadow-lg` 
                        : 'border-gray-700 hover:border-gray-600'
                    }
                `}
            >
                <div className="flex items-start justify-between gap-3 h-full">
                    <div className="flex-1 min-w-0 overflow-hidden">
                        {selectedOptions.length === 0 ? (
                            <div className="flex items-center gap-3">
                                <div className={`w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0 bg-gray-700/50`}>
                                    {icon || (
                                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                        </svg>
                                    )}
                                </div>
                                <p className="text-gray-500">{placeholder}</p>
                            </div>
                        ) : (
                            <div className="flex flex-wrap gap-2 overflow-hidden">
                                {selectedOptions.slice(0, maxDisplay).map((option) => (
                                    <span
                                        key={option.id}
                                        className={`
                                            inline-flex items-center gap-2 px-3 py-1.5 rounded-lg border text-sm
                                            ${colors.chip} transition-all duration-200
                                        `}
                                    >
                                        {/* Fan icon */}
                                        <svg className="w-4 h-4 animate-spin-slow" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                        </svg>
                                        <span className="font-medium">{option.label}</span>
                                        <button
                                            type="button"
                                            onClick={(e) => removeOption(option.id, e)}
                                            className={`ml-1 p-0.5 rounded-full ${colors.chipHover} transition-colors`}
                                        >
                                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                            </svg>
                                        </button>
                                    </span>
                                ))}
                                {selectedOptions.length > maxDisplay && (
                                    <span className={`inline-flex items-center px-3 py-1.5 rounded-lg border text-sm ${colors.chip}`}>
                                        +{selectedOptions.length - maxDisplay} more
                                    </span>
                                )}
                            </div>
                        )}
                    </div>

                    {/* Actions */}
                    <div className="flex items-center gap-2 flex-shrink-0">
                        {selectedOptions.length > 0 && (
                            <button
                                type="button"
                                onClick={clearAll}
                                className="p-1.5 rounded-lg bg-gray-700/50 hover:bg-red-500/30 text-gray-400 hover:text-red-400 transition-colors"
                                title="Clear all"
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                            </button>
                        )}
                        <div className={`
                            w-8 h-8 rounded-lg flex items-center justify-center
                            transition-all duration-300
                            ${isOpen ? colors.bg : 'bg-gray-700/30 group-hover:bg-gray-700/50'}
                        `}>
                            <svg
                                className={`w-4 h-4 text-gray-400 transition-transform duration-300 ${isOpen ? 'rotate-180' : ''}`}
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                            </svg>
                        </div>
                    </div>
                </div>

                {/* Selected count badge */}
                {selectedOptions.length > 0 && (
                    <div className="mt-2 pt-2 border-t border-gray-700/50">
                        <p className="text-xs text-gray-400">
                            <span className={`inline-flex items-center justify-center w-5 h-5 rounded-full ${colors.bg} text-white font-medium mr-2`}>
                                {selectedOptions.length}
                            </span>
                            cooler{selectedOptions.length !== 1 ? 's' : ''} selected
                        </p>
                    </div>
                )}
            </div>

            {/* Dropdown Menu - Rendered via Portal */}
            {isOpen && isMounted && createPortal(
                <div
                    ref={dropdownRef}
                    className={`
                        fixed bg-gray-900/95 backdrop-blur-xl border ${colors.border}
                        rounded-xl shadow-2xl shadow-black/50 overflow-hidden
                        dropdown-appear
                    `}
                    style={{
                        top: openDirection === 'up' ? 'auto' : dropdownPosition.top,
                        bottom: openDirection === 'up' ? `${window.innerHeight - dropdownPosition.top}px` : 'auto',
                        left: dropdownPosition.left,
                        width: dropdownPosition.width,
                        zIndex: 99999,
                        transformOrigin: openDirection === 'up' ? 'bottom center' : 'top center',
                        maxHeight: '380px',
                    }}
                >
                    {/* Search Input */}
                    <div className="p-3 border-b border-gray-700/50">
                        <div className="relative">
                            <svg
                                className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                            <input
                                ref={searchInputRef}
                                type="text"
                                value={search}
                                onChange={e => setSearch(e.target.value)}
                                placeholder="Search coolers..."
                                className="w-full pl-10 pr-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white placeholder-gray-500 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500"
                            />
                        </div>
                    </div>

                    {/* Options List */}
                    <div className="max-h-64 overflow-y-auto custom-scrollbar p-2">
                        {filteredOptions.length === 0 ? (
                            <div className="p-4 text-center text-gray-400">
                                <svg className="w-8 h-8 mx-auto mb-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M12 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                                <p>No coolers found</p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-2 gap-2">
                                {filteredOptions.map((option) => {
                                    const isSelected = value.includes(option.id);
                                    return (
                                        <button
                                            key={option.id}
                                            type="button"
                                            onClick={() => toggleOption(option.id)}
                                            className={`
                                                relative p-3 rounded-xl border-2 text-left transition-all duration-200
                                                ${isSelected 
                                                    ? `${colors.border} ${colors.bg} shadow-lg` 
                                                    : 'border-gray-700/50 hover:border-gray-600 hover:bg-gray-800/50'
                                                }
                                            `}
                                        >
                                            {/* Checkbox indicator */}
                                            <div className={`
                                                absolute top-2 right-2 w-5 h-5 rounded-md border-2 flex items-center justify-center
                                                transition-all duration-200
                                                ${isSelected 
                                                    ? `${colors.border} ${colors.bg}` 
                                                    : 'border-gray-600'
                                                }
                                            `}>
                                                {isSelected && (
                                                    <svg className="w-3 h-3 text-teal-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                                                    </svg>
                                                )}
                                            </div>

                                            {/* Fan Animation Icon */}
                                            <div className={`
                                                w-10 h-10 rounded-lg flex items-center justify-center mb-2
                                                ${isSelected ? colors.bg : 'bg-gray-700/50'}
                                            `}>
                                                <svg
                                                    className={`w-6 h-6 ${isSelected ? 'text-teal-400 animate-spin-slow' : 'text-gray-400'}`}
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                                </svg>
                                            </div>

                                            {/* Option content */}
                                            <p className={`font-medium text-sm ${isSelected ? 'text-white' : 'text-gray-200'}`}>
                                                {option.label}
                                            </p>
                                            {option.sublabel && (
                                                <p className="text-xs text-gray-400 mt-0.5">{option.sublabel}</p>
                                            )}

                                            {/* Specs on hover/select */}
                                            {option.specs && option.specs.length > 0 && isSelected && (
                                                <div className="mt-2 flex flex-wrap gap-1">
                                                    {option.specs.map((spec, idx) => (
                                                        <span
                                                            key={idx}
                                                            className="px-1.5 py-0.5 rounded text-xs bg-gray-800 text-gray-300"
                                                        >
                                                            {spec.value}
                                                        </span>
                                                    ))}
                                                </div>
                                            )}
                                        </button>
                                    );
                                })}
                            </div>
                        )}
                    </div>

                    {/* Footer */}
                    <div className="px-4 py-3 border-t border-gray-700/50 bg-gray-800/30 flex items-center justify-between">
                        <p className="text-xs text-gray-500">
                            {value.length} of {options.length} selected
                        </p>
                        <button
                            type="button"
                            onClick={() => {
                                setIsOpen(false);
                                setSearch('');
                            }}
                            className={`px-4 py-1.5 rounded-lg text-sm font-medium ${colors.bg} ${colors.border} border text-white hover:opacity-80 transition-opacity`}
                        >
                            Done
                        </button>
                    </div>
                </div>,
                document.body
            )}
        </div>
    );
};

export default MultiComponentSelect;


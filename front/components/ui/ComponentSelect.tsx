'use client';
import React, { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';

export interface SelectOption {
    id: number;
    label: string;
    sublabel?: string;
    icon?: React.ReactNode;
    specs?: { label: string; value: string }[];
}

interface ComponentSelectProps {
    label: string;
    placeholder: string;
    options: SelectOption[];
    value: number | undefined;
    onChange: (value: number | undefined) => void;
    required?: boolean;
    accentColor?: string;
    icon?: React.ReactNode;
}

const ComponentSelect: React.FC<ComponentSelectProps> = ({
    label,
    placeholder,
    options,
    value,
    onChange,
    required = false,
    accentColor = 'blue',
    icon,
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [search, setSearch] = useState('');
    const [highlightedIndex, setHighlightedIndex] = useState(0);
    const [dropdownPosition, setDropdownPosition] = useState({ top: 0, left: 0, width: 0 });
    const [isMounted, setIsMounted] = useState(false);
    const [openDirection, setOpenDirection] = useState<'down' | 'up'>('down');
    const containerRef = useRef<HTMLDivElement>(null);
    const buttonRef = useRef<HTMLButtonElement>(null);
    const searchInputRef = useRef<HTMLInputElement>(null);
    const dropdownRef = useRef<HTMLDivElement>(null);

    const selectedOption = options.find(opt => opt.id === value);

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

    // Reset highlighted index when filtering
    useEffect(() => {
        setHighlightedIndex(0);
    }, [search]);

    // Handle portal mounting (client-side only)
    useEffect(() => {
        setIsMounted(true);
    }, []);

    // Function to calculate and update dropdown position
    const updatePosition = () => {
        if (buttonRef.current) {
            const rect = buttonRef.current.getBoundingClientRect();
            const dropdownHeight = 320; // Approximate max height of dropdown
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

            // Add scroll and resize listeners
            window.addEventListener('scroll', updatePosition, true);
            window.addEventListener('resize', updatePosition);

            return () => {
                window.removeEventListener('scroll', updatePosition, true);
                window.removeEventListener('resize', updatePosition);
            };
        }
    }, [isOpen]);

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (!isOpen) {
            if (e.key === 'Enter' || e.key === ' ' || e.key === 'ArrowDown') {
                e.preventDefault();
                setIsOpen(true);
            }
            return;
        }

        switch (e.key) {
            case 'ArrowDown':
                e.preventDefault();
                setHighlightedIndex(prev =>
                    prev < filteredOptions.length - 1 ? prev + 1 : prev
                );
                break;
            case 'ArrowUp':
                e.preventDefault();
                setHighlightedIndex(prev => (prev > 0 ? prev - 1 : prev));
                break;
            case 'Enter':
                e.preventDefault();
                if (filteredOptions[highlightedIndex]) {
                    onChange(filteredOptions[highlightedIndex].id);
                    setIsOpen(false);
                    setSearch('');
                }
                break;
            case 'Escape':
                setIsOpen(false);
                setSearch('');
                break;
        }
    };

    const accentClasses: Record<string, { bg: string; border: string; ring: string; dot: string; hover: string; gradient: string }> = {
        red: {
            bg: 'bg-red-500/20',
            border: 'border-red-500/50',
            ring: 'ring-red-500',
            dot: 'bg-red-400',
            hover: 'hover:bg-red-500/10',
            gradient: 'from-red-500/20 to-transparent',
        },
        green: {
            bg: 'bg-green-500/20',
            border: 'border-green-500/50',
            ring: 'ring-green-500',
            dot: 'bg-green-400',
            hover: 'hover:bg-green-500/10',
            gradient: 'from-green-500/20 to-transparent',
        },
        blue: {
            bg: 'bg-blue-500/20',
            border: 'border-blue-500/50',
            ring: 'ring-blue-500',
            dot: 'bg-blue-400',
            hover: 'hover:bg-blue-500/10',
            gradient: 'from-blue-500/20 to-transparent',
        },
        yellow: {
            bg: 'bg-yellow-500/20',
            border: 'border-yellow-500/50',
            ring: 'ring-yellow-500',
            dot: 'bg-yellow-400',
            hover: 'hover:bg-yellow-500/10',
            gradient: 'from-yellow-500/20 to-transparent',
        },
        purple: {
            bg: 'bg-purple-500/20',
            border: 'border-purple-500/50',
            ring: 'ring-purple-500',
            dot: 'bg-purple-400',
            hover: 'hover:bg-purple-500/10',
            gradient: 'from-purple-500/20 to-transparent',
        },
        cyan: {
            bg: 'bg-cyan-500/20',
            border: 'border-cyan-500/50',
            ring: 'ring-cyan-500',
            dot: 'bg-cyan-400',
            hover: 'hover:bg-cyan-500/10',
            gradient: 'from-cyan-500/20 to-transparent',
        },
        orange: {
            bg: 'bg-orange-500/20',
            border: 'border-orange-500/50',
            ring: 'ring-orange-500',
            dot: 'bg-orange-400',
            hover: 'hover:bg-orange-500/10',
            gradient: 'from-orange-500/20 to-transparent',
        },
        indigo: {
            bg: 'bg-indigo-500/20',
            border: 'border-indigo-500/50',
            ring: 'ring-indigo-500',
            dot: 'bg-indigo-400',
            hover: 'hover:bg-indigo-500/10',
            gradient: 'from-indigo-500/20 to-transparent',
        },
        pink: {
            bg: 'bg-pink-500/20',
            border: 'border-pink-500/50',
            ring: 'ring-pink-500',
            dot: 'bg-pink-400',
            hover: 'hover:bg-pink-500/10',
            gradient: 'from-pink-500/20 to-transparent',
        },
        teal: {
            bg: 'bg-teal-500/20',
            border: 'border-teal-500/50',
            ring: 'ring-teal-500',
            dot: 'bg-teal-400',
            hover: 'hover:bg-teal-500/10',
            gradient: 'from-teal-500/20 to-transparent',
        },
    };

    const colors = accentClasses[accentColor] || accentClasses.blue;

    return (
        <div className="relative" ref={containerRef}>
            {/* Label */}
            <label className="block text-sm font-medium text-gray-300 mb-2">
                <span className="flex items-center gap-2">
                    <span className={`w-2 h-2 ${colors.dot} rounded-full`}></span>
                    {label}
                    {required && <span className="text-red-400">*</span>}
                </span>
            </label>

            {/* Trigger Button */}
            <button
                ref={buttonRef}
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                onKeyDown={handleKeyDown}
                className={`
                    w-full px-4 py-2.5 bg-gray-800/80 border rounded-xl text-left
                    transition-all duration-300 group h-[76px] flex flex-col justify-center
                    ${isOpen 
                        ? `${colors.border} ring-2 ${colors.ring} shadow-lg` 
                        : 'border-gray-700 hover:border-gray-600'
                    }
                    ${selectedOption ? 'text-white' : 'text-gray-400'}
                `}
            >
                <div className="flex items-center justify-between h-full">
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                        {/* Icon or colored indicator */}
                        <div className={`
                            w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0
                            transition-all duration-300
                            ${selectedOption ? colors.bg : 'bg-gray-700/50'}
                        `}>
                            {icon || (
                                <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                </svg>
                            )}
                        </div>

                        {/* Selected value or placeholder */}
                        <div className="flex-1 min-w-0">
                            {selectedOption ? (
                                <>
                                    <p className="font-medium truncate text-sm">{selectedOption.label}</p>
                                    {selectedOption.sublabel && (
                                        <p className="text-xs text-gray-400 truncate mt-0.5">{selectedOption.sublabel}</p>
                                    )}
                                </>
                            ) : (
                                <p className="text-gray-500">{placeholder}</p>
                            )}
                        </div>
                    </div>

                    {/* Dropdown arrow */}
                    <div className={`
                        w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0
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
            </button>

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
                        maxHeight: '320px',
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
                                onKeyDown={handleKeyDown}
                                placeholder="Search..."
                                className="w-full pl-10 pr-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white placeholder-gray-500 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>
                    </div>

                    {/* Options List */}
                    <div className="max-h-64 overflow-y-auto custom-scrollbar">
                        {filteredOptions.length === 0 ? (
                            <div className="p-4 text-center text-gray-400">
                                <svg className="w-8 h-8 mx-auto mb-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M12 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                                <p>No components found</p>
                            </div>
                        ) : (
                            filteredOptions.map((option, index) => (
                                <button
                                    key={`${option.id}-${index}`}
                                    type="button"
                                    onClick={() => {
                                        onChange(option.id);
                                        setIsOpen(false);
                                        setSearch('');
                                    }}
                                    onMouseEnter={() => setHighlightedIndex(index)}
                                    className={`
                                        w-full px-4 py-3 text-left transition-all duration-150
                                        flex items-center gap-3 group/option
                                        ${highlightedIndex === index 
                                            ? `bg-gradient-to-r ${colors.gradient}` 
                                            : 'hover:bg-gray-800/50'
                                        }
                                        ${value === option.id ? 'bg-gray-800/80' : ''}
                                    `}
                                >
                                    {/* Selection indicator */}
                                    <div className={`
                                        w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0
                                        transition-all duration-200
                                        ${value === option.id 
                                            ? `${colors.border} ${colors.bg}` : ''
                                        }
                                    `}>
                                    </div>

                                    {/* Option content */}
                                    <div className="flex-1 min-w-0">
                                        <p className={`font-medium truncate ${value === option.id ? 'text-white' : 'text-gray-200'}`}>
                                            {option.label}
                                        </p>
                                        {option.sublabel && (
                                            <p className="text-xs text-gray-400 truncate">{option.sublabel}</p>
                                        )}
                                        {option.specs && option.specs.length > 0 && highlightedIndex === index && (
                                            <div className="mt-2 flex flex-wrap gap-1">
                                                {option.specs.map((spec, idx) => (
                                                    <span
                                                        key={idx}
                                                        className="px-2 py-0.5 rounded text-xs bg-gray-800 text-gray-300"
                                                    >
                                                        {spec.label}: {spec.value}
                                                    </span>
                                                ))}
                                            </div>
                                        )}
                                    </div>

                                    {/* Checkmark for selected */}
                                    {value === option.id && (
                                        <svg className={`w-5 h-5 ${colors.dot.replace('bg-', 'text-')}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                        </svg>
                                    )}
                                </button>
                            ))
                        )}
                    </div>

                    {/* Footer with count */}
                    <div className="px-4 py-2 border-t border-gray-700/50 bg-gray-800/30">
                        <p className="text-xs text-gray-500">
                            {filteredOptions.length} of {options.length} components
                        </p>
                    </div>
                </div>,
                document.body
            )}

            {/* Hidden input for form validation */}
            {required && (
                <input
                    type="hidden"
                    value={value ?? ''}
                    required
                />
            )}
        </div>
    );
};

export default ComponentSelect;


'use client';
import React, { useState } from 'react';
import { useCreatePCMutation } from '@/features/pc/pcApi';
import {
    useComponentManager,
    useComponentDetail,
} from '@/features/components/useComponentHooks';
import {
    CPUDTO,
    GPUDTO,
    RAMKitDTO,
    StorageDTO,
    PSUDTO,
    CaseCoolerDTO,
    CPUCoolerDTO,
    MotherBoardDTO,
    CaseDTO,
    CreatePCDTO,
    VisibilityState,
} from '@/shared/types';

function CreatePc() {
    // Form state
    const [form, setForm] = useState<Partial<CreatePCDTO>>({
        name: '',
        description: '',
        purpose: '',
        cpuId: undefined,
        gpuId: undefined,
        storageId: undefined,
        ramKitId: undefined,
        psuId: undefined,
        coolerIds: [],
        cpuCoolerId: undefined,
        motherboardId: undefined,
        pcCaseId: undefined,
        location: '',
        visibility: VisibilityState.PUBLIC,
    });

    // Use useComponentManager for all component lists
    const { items: cpus = [], isLoading: loadingCpus } =
        useComponentManager<CPUDTO>('cpu');
    const { items: gpus = [], isLoading: loadingGpus } =
        useComponentManager<GPUDTO>('gpu');
    const { items: ramKits = [], isLoading: loadingRamKits } =
        useComponentManager<RAMKitDTO>('ram-kit');
    const { items: storages = [], isLoading: loadingStorages } =
        useComponentManager<StorageDTO>('storage');
    const { items: psus = [], isLoading: loadingPsus } =
        useComponentManager<PSUDTO>('psu');
    const { items: caseCoolers = [], isLoading: loadingCaseCoolers } =
        useComponentManager<CaseCoolerDTO>('case-cooler');
    const { items: cpuCoolers = [], isLoading: loadingCpuCoolers } =
        useComponentManager<CPUCoolerDTO>('cpu-cooler');
    const { items: motherboards = [], isLoading: loadingMotherboards } =
        useComponentManager<MotherBoardDTO>('motherboard');
    const { items: cases = [], isLoading: loadingCases } =
        useComponentManager<CaseDTO>('case');

    const [createPC, { isLoading, isSuccess, isError, error }] =
        useCreatePCMutation();

    const handleChange = (
        e: React.ChangeEvent<
            HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
        >,
    ) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSelectNumber = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value ? Number(value) : undefined,
        }));
    };

    const handleCoolerIdsChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedOptions = Array.from(e.target.selectedOptions, option =>
            Number(option.value),
        );
        setForm(prev => ({ ...prev, coolerIds: selectedOptions }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await createPC(form as CreatePCDTO);
    };

    // Always call the hook unconditionally - it will skip the query if cpuId is undefined
    const cpuDetail = useComponentDetail<CPUDTO>('cpu', form.cpuId);

    const isLoadingComponents =
        loadingCpus ||
        loadingGpus ||
        loadingRamKits ||
        loadingStorages ||
        loadingPsus ||
        loadingCaseCoolers ||
        loadingCpuCoolers ||
        loadingMotherboards ||
        loadingCases;

    const inputClasses =
        'w-full px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200';
    const selectClasses =
        'w-full px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 cursor-pointer appearance-none';
    const labelClasses = 'block text-sm font-medium text-gray-300 mb-2';

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-12 px-4">
            <div className="max-w-4xl mx-auto">
                {/* Header */}
                <div className="text-center mb-10">
                    <h1 className="text-4xl font-bold text-white mb-3">
                        Build Your Dream PC
                    </h1>
                    <p className="text-gray-400 text-lg">
                        Configure your custom PC build with premium components
                    </p>
                </div>

                {/* Loading Overlay */}
                {isLoadingComponents && (
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                        <div className="bg-gray-800 rounded-2xl p-8 flex flex-col items-center gap-4">
                            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                            <p className="text-white font-medium">
                                Loading components...
                            </p>
                        </div>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-8">
                    {/* Basic Info Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 bg-blue-500/20 rounded-lg flex items-center justify-center">
                                <svg
                                    className="w-5 h-5 text-blue-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                                    />
                                </svg>
                            </div>
                            <h2 className="text-xl font-semibold text-white">
                                Basic Information
                            </h2>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className={labelClasses}>PC Name</label>
                                <input
                                    name="name"
                                    placeholder="My Awesome Build"
                                    value={form.name}
                                    onChange={handleChange}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div>
                                <label className={labelClasses}>Purpose</label>
                                <input
                                    name="purpose"
                                    placeholder="Gaming, Workstation, etc."
                                    value={form.purpose}
                                    onChange={handleChange}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div className="md:col-span-2">
                                <label className={labelClasses}>
                                    Description
                                </label>
                                <textarea
                                    name="description"
                                    placeholder="Describe your build..."
                                    value={form.description}
                                    onChange={handleChange}
                                    required
                                    rows={3}
                                    className={`${inputClasses} resize-none`}
                                />
                            </div>
                            <div>
                                <label className={labelClasses}>Location</label>
                                <input
                                    name="location"
                                    placeholder="City, Country"
                                    value={form.location}
                                    onChange={handleChange}
                                    required
                                    className={inputClasses}
                                />
                            </div>
                            <div>
                                <label className={labelClasses}>
                                    Visibility
                                </label>
                                <div className="relative">
                                    <select
                                        name="visibility"
                                        value={form.visibility}
                                        onChange={handleChange}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value={VisibilityState.PUBLIC}>
                                            🌐 Public
                                        </option>
                                        <option value={VisibilityState.PRIVATE}>
                                            🔒 Private
                                        </option>
                                        <option
                                            value={VisibilityState.UNLISTED}
                                        >
                                            🔗 Unlisted
                                        </option>
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Core Components Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 bg-purple-500/20 rounded-lg flex items-center justify-center">
                                <svg
                                    className="w-5 h-5 text-purple-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"
                                    />
                                </svg>
                            </div>
                            <h2 className="text-xl font-semibold text-white">
                                Core Components
                            </h2>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {/* CPU */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-red-400 rounded-full"></span>
                                        CPU (Processor)
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="cpuId"
                                        value={form.cpuId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select CPU</option>
                                        {cpus.map((cpu: CPUDTO) => (
                                            <option key={cpu.id} value={cpu.id}>
                                                {cpu.cpuBrand} {cpu.cpuModel}
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* GPU */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-green-400 rounded-full"></span>
                                        GPU (Graphics Card)
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="gpuId"
                                        value={form.gpuId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select GPU</option>
                                        {gpus.map((gpu: GPUDTO) => (
                                            <option key={gpu.id} value={gpu.id}>
                                                {gpu.gpuBrand} {gpu.gpuModel} (
                                                {gpu.gpuMemoryGb}GB)
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* Motherboard */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-yellow-400 rounded-full"></span>
                                        Motherboard
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="motherboardId"
                                        value={form.motherboardId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">
                                            Select Motherboard
                                        </option>
                                        {motherboards.map(
                                            (mb: MotherBoardDTO) => (
                                                <option
                                                    key={mb.id}
                                                    value={mb.id}
                                                >
                                                    {mb.motherboardBrand}{' '}
                                                    {mb.motherboardModel}
                                                </option>
                                            ),
                                        )}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* RAM */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-blue-400 rounded-full"></span>
                                        RAM Kit
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="ramKitId"
                                        value={form.ramKitId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select RAM Kit</option>
                                        {ramKits.map((ram: RAMKitDTO) => (
                                            <option key={ram.id} value={ram.id}>
                                                {ram.ramType} {ram.ramSizeGb}GB
                                                @ {ram.ramSpeedMhz}MHz
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Storage & Power Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 bg-orange-500/20 rounded-lg flex items-center justify-center">
                                <svg
                                    className="w-5 h-5 text-orange-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M13 10V3L4 14h7v7l9-11h-7z"
                                    />
                                </svg>
                            </div>
                            <h2 className="text-xl font-semibold text-white">
                                Storage & Power
                            </h2>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {/* Storage */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-cyan-400 rounded-full"></span>
                                        Storage
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="storageId"
                                        value={form.storageId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select Storage</option>
                                        {storages.map((storage: StorageDTO) => (
                                            <option
                                                key={storage.id}
                                                value={storage.id}
                                            >
                                                {storage.storageType}{' '}
                                                {storage.storageTotalGb}GB
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* PSU */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-orange-400 rounded-full"></span>
                                        Power Supply (PSU)
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="psuId"
                                        value={form.psuId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select PSU</option>
                                        {psus.map((psu: PSUDTO) => (
                                            <option key={psu.id} value={psu.id}>
                                                {psu.psuModel} ({psu.psuWattage}
                                                W)
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Cooling & Case Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 bg-cyan-500/20 rounded-lg flex items-center justify-center">
                                <svg
                                    className="w-5 h-5 text-cyan-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                                    />
                                </svg>
                            </div>
                            <h2 className="text-xl font-semibold text-white">
                                Cooling & Case
                            </h2>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {/* CPU Cooler */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-indigo-400 rounded-full"></span>
                                        CPU Cooler
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="cpuCoolerId"
                                        value={form.cpuCoolerId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">
                                            Select CPU Cooler
                                        </option>
                                        {cpuCoolers.map(
                                            (cooler: CPUCoolerDTO) => (
                                                <option
                                                    key={cooler.id}
                                                    value={cooler.id}
                                                >
                                                    {cooler.coolingType} -{' '}
                                                    {cooler.fanCount} fan(s)
                                                </option>
                                            ),
                                        )}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* Case */}
                            <div className="relative">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-pink-400 rounded-full"></span>
                                        PC Case
                                    </span>
                                </label>
                                <div className="relative">
                                    <select
                                        name="pcCaseId"
                                        value={form.pcCaseId ?? ''}
                                        onChange={handleSelectNumber}
                                        required
                                        className={selectClasses}
                                    >
                                        <option value="">Select Case</option>
                                        {cases.map((pcCase: CaseDTO) => (
                                            <option
                                                key={pcCase.id}
                                                value={pcCase.id}
                                            >
                                                {pcCase.caseModel} (
                                                {pcCase.caseColor})
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                                        <svg
                                            className="w-5 h-5 text-gray-400"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth={2}
                                                d="M19 9l-7 7-7-7"
                                            />
                                        </svg>
                                    </div>
                                </div>
                            </div>

                            {/* Case Coolers - Multi-select */}
                            <div className="md:col-span-2">
                                <label className={labelClasses}>
                                    <span className="flex items-center gap-2">
                                        <span className="w-2 h-2 bg-teal-400 rounded-full"></span>
                                        Case Coolers (Hold Ctrl/Cmd to select
                                        multiple)
                                    </span>
                                </label>
                                <select
                                    name="coolerIds"
                                    multiple
                                    value={form.coolerIds?.map(String) ?? []}
                                    onChange={handleCoolerIdsChange}
                                    className={`${selectClasses} min-h-[120px]`}
                                >
                                    {caseCoolers.map(
                                        (cooler: CaseCoolerDTO) => (
                                            <option
                                                key={cooler.id}
                                                value={cooler.id}
                                                className="py-2"
                                            >
                                                {cooler.fanSize}mm -{' '}
                                                {cooler.coolingColor ||
                                                    'Standard'}
                                            </option>
                                        ),
                                    )}
                                </select>
                                <p className="text-xs text-gray-500 mt-2">
                                    Selected: {form.coolerIds?.length || 0}{' '}
                                    cooler(s)
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Selected CPU Details Preview */}
                    {cpuDetail && cpuDetail.data && (
                        <div className="bg-gradient-to-r from-blue-900/30 to-purple-900/30 rounded-2xl p-6 border border-blue-500/30">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="w-10 h-10 bg-blue-500/20 rounded-lg flex items-center justify-center">
                                    <svg
                                        className="w-5 h-5 text-blue-400"
                                        fill="none"
                                        stroke="currentColor"
                                        viewBox="0 0 24 24"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth={2}
                                            d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"
                                        />
                                    </svg>
                                </div>
                                <h3 className="text-lg font-semibold text-white">
                                    Selected CPU Specifications
                                </h3>
                            </div>
                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                <div className="bg-gray-800/50 rounded-lg p-3">
                                    <p className="text-xs text-gray-400 mb-1">
                                        Brand
                                    </p>
                                    <p className="text-white font-medium">
                                        {cpuDetail.data.cpuBrand}
                                    </p>
                                </div>
                                <div className="bg-gray-800/50 rounded-lg p-3">
                                    <p className="text-xs text-gray-400 mb-1">
                                        Model
                                    </p>
                                    <p className="text-white font-medium">
                                        {cpuDetail.data.cpuModel}
                                    </p>
                                </div>
                                <div className="bg-gray-800/50 rounded-lg p-3">
                                    <p className="text-xs text-gray-400 mb-1">
                                        Cores
                                    </p>
                                    <p className="text-white font-medium">
                                        {cpuDetail.data.cpuCores}
                                    </p>
                                </div>
                                <div className="bg-gray-800/50 rounded-lg p-3">
                                    <p className="text-xs text-gray-400 mb-1">
                                        Threads
                                    </p>
                                    <p className="text-white font-medium">
                                        {cpuDetail.data.cpuThreads}
                                    </p>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Status Messages */}
                    {isSuccess && (
                        <div className="bg-green-900/30 border border-green-500/50 rounded-xl p-4 flex items-center gap-3">
                            <div className="w-8 h-8 bg-green-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                                <svg
                                    className="w-5 h-5 text-green-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M5 13l4 4L19 7"
                                    />
                                </svg>
                            </div>
                            <p className="text-green-400 font-medium">
                                PC created successfully!
                            </p>
                        </div>
                    )}

                    {isError && (
                        <div className="bg-red-900/30 border border-red-500/50 rounded-xl p-4 flex items-center gap-3">
                            <div className="w-8 h-8 bg-red-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                                <svg
                                    className="w-5 h-5 text-red-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M6 18L18 6M6 6l12 12"
                                    />
                                </svg>
                            </div>
                            <p className="text-red-400 font-medium">
                                Error:{' '}
                                {typeof error === 'object' &&
                                error &&
                                'data' in error &&
                                typeof error.data === 'object' &&
                                error.data &&
                                'message' in error.data
                                    ? String(
                                          (error.data as { message?: string })
                                              .message,
                                      )
                                    : 'Unknown error'}
                            </p>
                        </div>
                    )}

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full py-4 px-6 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 disabled:from-gray-600 disabled:to-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-xl shadow-lg shadow-blue-500/25 hover:shadow-blue-500/40 transition-all duration-300 flex items-center justify-center gap-3"
                    >
                        {isLoading ? (
                            <>
                                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                Creating your PC...
                            </>
                        ) : (
                            <>
                                <svg
                                    className="w-5 h-5"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                                    />
                                </svg>
                                Create PC Build
                            </>
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default CreatePc;

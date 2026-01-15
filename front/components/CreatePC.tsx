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
import ComponentSelect, { SelectOption } from '@/components/ui/ComponentSelect';
import MultiComponentSelect, { MultiSelectOption } from '@/components/ui/MultiComponentSelect';

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

    // Convert components to SelectOption format
    const cpuOptions: SelectOption[] = cpus.map((cpu: CPUDTO) => ({
        id: cpu.id,
        label: `${cpu.cpuBrand} ${cpu.cpuModel}`,
        sublabel: `${cpu.cpuCores} Cores / ${cpu.cpuThreads} Threads`,
        specs: [
            { label: 'Cores', value: String(cpu.cpuCores) },
            { label: 'Threads', value: String(cpu.cpuThreads) },
            { label: 'Base Clock', value: `${cpu.cpuBaseClockGhz} GHz` },
            { label: 'Boost Clock', value: `${cpu.cpuBoostClockGhz} GHz` },
            { label: 'Socket Type', value: cpu.socketType },
            { label: 'Power Draw', value: `${cpu.powerDraw}W` },
        ],
    }));

    const gpuOptions: SelectOption[] = gpus.map((gpu: GPUDTO) => ({
        id: gpu.id,
        label: `${gpu.gpuBrand} ${gpu.gpuModel}`,
        sublabel: `${gpu.gpuMemoryGb}GB VRAM`,
        specs: [
            { label: 'Memory', value: `${gpu.gpuMemoryGb}GB` },
            { label: 'Power Draw', value: `${gpu.powerDraw}W` },
        ],
    }));

    const motherboardOptions: SelectOption[] = motherboards.map((mb: MotherBoardDTO) => ({
        id: mb.id,
        label: `${mb.motherboardBrand} ${mb.motherboardModel}`,
        sublabel: `${mb.chipset} • ${mb.socketType} • ${mb.memoryType}`,
        specs: [
            { label: 'Chipset', value: mb.chipset },
            { label: 'Socket', value: mb.socketType },
            { label: 'Memory Type', value: mb.memoryType },
        ],
    }));

    const ramOptions: SelectOption[] = ramKits.map((ram: RAMKitDTO) => ({
        id: ram.id,
        label: `${ram.ramType} ${ram.ramSizeGb}GB`,
        sublabel: `${ram.ramSpeedMhz}MHz • ${ram.ramSticksCount} stick(s)`,
        specs: [
            { label: 'Type', value: ram.ramType },
            { label: 'Size', value: `${ram.ramSizeGb}GB` },
            { label: 'Speed', value: `${ram.ramSpeedMhz}MHz` },
            { label: 'Sticks', value: String(ram.ramSticksCount) },
        ],
    }));

    const storageOptions: SelectOption[] = storages.map((storage: StorageDTO) => ({
        id: storage.id,
        label: `${storage.storageType} ${storage.storageTotalGb}GB`,
        sublabel: `drive(s)`,
        specs: [
            { label: 'Type', value: storage.storageType },
            { label: 'Capacity', value: `${storage.storageTotalGb}GB` },
        ],
    }));

    const psuOptions: SelectOption[] = psus.map((psu: PSUDTO) => ({
        id: psu.id,
        label: psu.psuModel,
        sublabel: `${psu.psuWattage}W`,
        specs: [
            { label: 'Wattage', value: `${psu.psuWattage}W` },
        ],
    }));

    const caseCoolerOptions: MultiSelectOption[] = caseCoolers.map((cooler: CaseCoolerDTO) => ({
        id: cooler.id,
        label: `${cooler.fanSize}mm Fan`,
        sublabel: cooler.coolingColor || 'Standard',
        specs: [
            { label: 'Size', value: `${cooler.fanSize}mm` },
            { label: 'Color', value: cooler.coolingColor || 'Standard' },
        ],
    }));

    const cpuCoolerOptions: SelectOption[] = cpuCoolers.map((cooler: CPUCoolerDTO) => ({
        id: cooler.id,
        label: `${cooler.coolingType} Cooler`,
        sublabel: `${cooler.fanCount} fan(s) • ${cooler.maxTDP}W TDP`,
        specs: [
            { label: 'Type', value: cooler.coolingType },
            { label: 'Fans', value: String(cooler.fanCount) },
            { label: 'Socket', value: cooler.cpuSocket },
            { label: 'TDP', value: `${cooler.maxTDP}W` },
            { label: 'Case Slots', value: String(cooler.caseCoolerSlotsRequired) },
        ],
    }));

    const caseOptions: SelectOption[] = cases.map((pcCase: CaseDTO) => ({
        id: pcCase.id,
        label: pcCase.caseModel || 'Unknown Case',
        sublabel: `${pcCase.caseColor || 'N/A'} • ${pcCase.rgbSetup || 'No RGB'}`,
        specs: [
            { label: 'Model', value: pcCase.caseModel || 'N/A' },
            { label: 'Color', value: pcCase.caseColor || 'N/A' },
            { label: 'RGB', value: pcCase.rgbSetup || 'None' },
        ],
    }));

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
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl overflow-visible min-h-[320px]">
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

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 overflow-visible">
                            {/* CPU */}
                            <ComponentSelect
                                label="CPU (Processor)"
                                placeholder="Select CPU"
                                options={cpuOptions}
                                value={form.cpuId}
                                onChange={(value) => setForm(prev => ({ ...prev, cpuId: value }))}
                                required
                                accentColor="red"
                                icon={
                                    <svg className="w-5 h-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
                                    </svg>
                                }
                            />

                            {/* GPU */}
                            <ComponentSelect
                                label="GPU (Graphics Card)"
                                placeholder="Select GPU"
                                options={gpuOptions}
                                value={form.gpuId}
                                onChange={(value) => setForm(prev => ({ ...prev, gpuId: value }))}
                                required
                                accentColor="green"
                                icon={
                                    <svg className="w-5 h-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7m0 10a2 2 0 002 2h2a2 2 0 002-2V7a2 2 0 00-2-2h-2a2 2 0 00-2 2" />
                                    </svg>
                                }
                            />

                            {/* Motherboard */}
                            <ComponentSelect
                                label="Motherboard"
                                placeholder="Select Motherboard"
                                options={motherboardOptions}
                                value={form.motherboardId}
                                onChange={(value) => setForm(prev => ({ ...prev, motherboardId: value }))}
                                required
                                accentColor="yellow"
                                icon={
                                    <svg className="w-5 h-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z" />
                                    </svg>
                                }
                            />

                            {/* RAM */}
                            <ComponentSelect
                                label="RAM Kit"
                                placeholder="Select RAM Kit"
                                options={ramOptions}
                                value={form.ramKitId}
                                onChange={(value) => setForm(prev => ({ ...prev, ramKitId: value }))}
                                required
                                accentColor="blue"
                                icon={
                                    <svg className="w-5 h-5 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 01-2 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2h2a2 2 0 012 2m0 10V7a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                                    </svg>
                                }
                            />
                        </div>
                    </div>

                    {/* Storage & Power Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl overflow-visible min-h-[220px]">
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

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 overflow-visible">
                            {/* Storage */}
                            <ComponentSelect
                                label="Storage"
                                placeholder="Select Storage"
                                options={storageOptions}
                                value={form.storageId}
                                onChange={(value) => setForm(prev => ({ ...prev, storageId: value }))}
                                required
                                accentColor="cyan"
                                icon={
                                    <svg className="w-5 h-5 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4" />
                                    </svg>
                                }
                            />

                            {/* PSU */}
                            <ComponentSelect
                                label="Power Supply (PSU)"
                                placeholder="Select PSU"
                                options={psuOptions}
                                value={form.psuId}
                                onChange={(value) => setForm(prev => ({ ...prev, psuId: value }))}
                                required
                                accentColor="orange"
                                icon={
                                    <svg className="w-5 h-5 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                                    </svg>
                                }
                            />
                        </div>
                    </div>

                    {/* Cooling & Case Card */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700/50 shadow-xl overflow-visible min-h-[320px]">
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

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 overflow-visible">
                            {/* CPU Cooler */}
                            <ComponentSelect
                                label="CPU Cooler"
                                placeholder="Select CPU Cooler"
                                options={cpuCoolerOptions}
                                value={form.cpuCoolerId}
                                onChange={(value) => setForm(prev => ({ ...prev, cpuCoolerId: value }))}
                                required
                                accentColor="indigo"
                                icon={
                                    <svg className="w-5 h-5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                    </svg>
                                }
                            />

                            {/* Case */}
                            <ComponentSelect
                                label="PC Case"
                                placeholder="Select Case"
                                options={caseOptions}
                                value={form.pcCaseId}
                                onChange={(value) => setForm(prev => ({ ...prev, pcCaseId: value }))}
                                required
                                accentColor="pink"
                                icon={
                                    <svg className="w-5 h-5 text-pink-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2H5a2 2 0 00-2 2v2M7 7h10" />
                                    </svg>
                                }
                            />

                            {/* Case Coolers - Multi-select */}
                            <div className="md:col-span-2">
                                <MultiComponentSelect
                                    label="Case Coolers"
                                    placeholder="Click to add cooling fans..."
                                    options={caseCoolerOptions}
                                    value={form.coolerIds || []}
                                    onChange={(value) => setForm(prev => ({ ...prev, coolerIds: value }))}
                                    accentColor="teal"
                                    icon={
                                        <svg className="w-5 h-5 text-teal-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                        </svg>
                                    }
                                />
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

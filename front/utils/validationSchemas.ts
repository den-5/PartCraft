import { z } from 'zod';

export const loginSchema = z.object({
    email: z
        .string()
        .min(1, 'Email is required')
        .email('Invalid email format')
        .regex(/^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/, 'TLD must be at least 2 characters'),
    password: z
        .string()
        .min(8, 'Password must be at least 8 characters')
        .regex(/[0-9]/, 'Must contain at least 1 digit')
        .regex(/[a-z]/, 'Must contain at least 1 lowercase letter')
        .regex(/[A-Z]/, 'Must contain at least 1 uppercase letter')
        .regex(/[^a-zA-Z0-9]/, 'Must contain at least 1 special character')
        .regex(/^\S*$/, 'Must not contain spaces'),
});

export const signUpSchema = z.object({
    username: z
        .string()
        .min(5, 'Username must be at least 5 characters')
        .max(20, 'Username must be at most 20 characters')
        .regex(/^[a-zA-Z0-9]+$/, 'Only alphanumeric characters allowed'),
    email: z
        .string()
        .min(1, 'Email is required')
        .email('Invalid email format')
        .regex(/^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/, 'TLD must be at least 2 characters'),
    password: z
        .string()
        .min(8, 'Password must be at least 8 characters')
        .regex(/[0-9]/, 'Must contain at least 1 digit')
        .regex(/[a-z]/, 'Must contain at least 1 lowercase letter')
        .regex(/[A-Z]/, 'Must contain at least 1 uppercase letter')
        .regex(/[^a-zA-Z0-9]/, 'Must contain at least 1 special character')
        .regex(/^\S*$/, 'Must not contain spaces'),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type SignUpFormData = z.infer<typeof signUpSchema>;

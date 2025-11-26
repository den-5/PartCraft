import { FetchBaseQueryError } from '@reduxjs/toolkit/query';
import { SerializedError } from '@reduxjs/toolkit';

/**
 * 1. Type Guard for FetchBaseQueryError (HTTP Status Errors)
 * * This is the most common error type, containing 'status' and 'data' (the server body).
 * Use this to safely check for 4xx or 5xx responses.
 */
export function isFetchBaseQueryError(
    error: unknown,
): error is FetchBaseQueryError {
    return typeof error === 'object' && error != null && 'status' in error;
}

/**
 * 2. Type Guard for a generic object with a 'message' property
 * * This helps handle standard JavaScript Error objects or other simple serialized errors.
 */
export function isErrorWithMessage(
    error: unknown,
): error is { message: string } {
    return (
        typeof error === 'object' &&
        error != null &&
        'message' in error &&
        typeof (error as any).message === 'string'
    );
}

/**
 * 3. Utility Function to Extract Error Message
 * * This function consolidates the logic for determining the best error message
 * to show to the user, prioritizing the server's message.
 * * @param error The unknown error object caught by .catch()
 * @returns The best human-readable error message as a string
 */
export function getErrorMessage(error: unknown): string {
    if (isFetchBaseQueryError(error)) {
        // Attempt to extract a custom message from the server's response body
        // We assume the body might contain a JSON object with a 'message' field.
        const serverMessage = (error.data as any)?.message;

        if (serverMessage && typeof serverMessage === 'string') {
            return serverMessage;
        }

        // If no custom message, show the HTTP status/type
        if (typeof error.status === 'number') {
            return `Request failed with status: ${error.status}`;
        }

        // For internal RTK Query statuses like "PARSING_ERROR"
        return `Request failed: ${error.status}`;
    } else if (isErrorWithMessage(error)) {
        // For standard JS errors (e.g., NetworkError, Promise rejection)
        return error.message;
    }

    // Fallback for truly unknown errors
    return 'An unexpected error occurred.';
}

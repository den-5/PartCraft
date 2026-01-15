# 1. Use an official Node.js runtime as a parent image
FROM node:20-alpine AS builder

# 2. Set the working directory
WORKDIR /app

# 3. Copy package.json and install dependencies
COPY package.json package-lock.json* ./
# Note: If you use yarn or pnpm, copy those lockfiles instead
RUN npm ci

# 4. Copy the rest of the application code
COPY . .

# 5. Build the Next.js app
# This creates the .next folder
RUN npm run build

# --- Production Stage ---
FROM node:20-alpine AS runner
WORKDIR /app

# 6. Set environment to production
ENV NODE_ENV production

# 7. Copy necessary files from builder stage
#COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 8. Expose the port Next.js runs on
EXPOSE 3000

# 9. Start the application
CMD ["npm", "start"]
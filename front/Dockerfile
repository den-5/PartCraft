# 1. Use an official Node.js runtime as a parent image
FROM node:20-alpine AS builder

# 2. Set the working directory
WORKDIR /app

# 3. Copy package.json and install dependencies
COPY package.json package-lock.json* ./
RUN npm ci

# 4. Copy the rest of the application code
COPY . .

# 👇 5. CRITICAL FIX: Accept the variable and set it for the build
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

# 6. Build the Next.js app
# Now 'npm run build' can see the IP address and "bake" it into the JS
RUN npm run build

# --- Production Stage ---
FROM node:20-alpine AS runner
WORKDIR /app

# 7. Set environment to production
ENV NODE_ENV production

# 8. Copy necessary files from builder stage
# (Ensure public folder is copied if you have static assets like images!)
#COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 9. Expose the port Next.js runs on
EXPOSE 3000

# 10. Start the application
CMD ["npm", "start"]
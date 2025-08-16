# Implement Posts Feature for Homepage

## Problem Analysis
The eassy homepage has an updates section that currently shows static/hardcoded articles. We need to implement dynamic posts fetching from the backend API using cursor pagination and load more functionality, similar to the super admin implementation. The posts should fit the homepage UI design and be responsive.

## Plan

### Todo Items
- [ ] Analyze current homepage updates page structure and UI components
- [ ] Create PostData interface/type for homepage
- [ ] Create API service to fetch posts from backend with cursor pagination
- [ ] Create custom hook for posts data fetching with infinite scroll
- [ ] Replace static articles with dynamic posts component
- [ ] Implement responsive design and ensure UI consistency
- [ ] Add load more button functionality
- [ ] Test the complete implementation

### Implementation Strategy
1. **Current State Analysis**: The updates page currently uses hardcoded articles array with featured/regular article display
2. **API Integration**: Use the existing `/api/posts` endpoint which supports cursor pagination
3. **Data Management**: Create hook similar to super admin but adapted for homepage needs
4. **UI Adaptation**: Replace static articles with dynamic posts while maintaining the existing design
5. **Pagination**: Implement cursor pagination with load more button like super admin

### Technical Details
- **Backend API**: `/api/posts` endpoint already exists in `routes/api.php` line 166
- **Frontend Framework**: Next.js with Chakra UI and TypeScript
- **Libraries**: date-fns already available for date formatting
- **Pattern**: Follow similar structure to super admin but adapted for homepage styling

## Notes
- Keep changes simple and focused on replacing static content with dynamic data
- Preserve existing UI/UX design and animations
- Ensure responsive design works properly on all devices
- Use existing date formatting utilities (date-fns)
- Maintain the featured post concept if the backend supports it
- Follow homepage design patterns and component structure

## ✅ Review

### Summary of Changes Made
All todo items have been completed successfully. The implementation transformed the static updates page into a dynamic posts system while maintaining the exact same UI/UX design.

### Files Created:
1. **`types/post.ts`** - TypeScript interfaces for PostData and PostsResponse
2. **`services/posts.ts`** - API service for fetching posts with error handling 
3. **`hooks/use-posts.ts`** - Custom hook managing posts state and pagination logic
4. **`.env.example`** - Environment variable template for API configuration

### Files Modified:
1. **`app/(marketing)/updates/page.tsx`** - Replaced static articles array with dynamic posts from API, added loading states and load more functionality

### Key Implementation Details:
- **API Integration**: Used existing `/api/posts` endpoint with standard Laravel pagination
- **Data Transformation**: API data mapped to existing UI structure (first post = featured)
- **Error Handling**: Loading states and error messages with graceful fallbacks
- **Pagination**: Load more button with proper loading states and cursor pagination
- **Date Formatting**: German locale formatting using existing date-fns library
- **UI Consistency**: Preserved exact same design, animations, and responsive behavior
- **Build Verification**: Successfully built without TypeScript errors

### Environment Setup Required:
- Set `NEXT_PUBLIC_API_URL` environment variable to point to the backend API (default: http://localhost:8000)

The implementation is simple, minimal, and follows all requirements from CLAUDE.md - keeping changes focused and avoiding complex modifications while successfully replacing static content with dynamic API data.
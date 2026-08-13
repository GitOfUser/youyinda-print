---
name: Warm Professionalism
colors:
  surface: '#f9f9ff'
  surface-dim: '#d7dae5'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#ebedf9'
  surface-container-high: '#e5e8f3'
  surface-container-highest: '#dfe2ed'
  on-surface: '#181c23'
  on-surface-variant: '#584235'
  inverse-surface: '#2c3039'
  inverse-on-surface: '#eef0fc'
  outline: '#8b7263'
  outline-variant: '#dfc0af'
  surface-tint: '#984800'
  primary: '#984800'
  on-primary: '#ffffff'
  primary-container: '#ff7d00'
  on-primary-container: '#5d2a00'
  inverse-primary: '#ffb689'
  secondary: '#0047d0'
  on-secondary: '#ffffff'
  secondary-container: '#175dff'
  on-secondary-container: '#eeefff'
  tertiary: '#006e16'
  on-tertiary: '#ffffff'
  tertiary-container: '#17bb31'
  on-tertiary-container: '#004309'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbc8'
  primary-fixed-dim: '#ffb689'
  on-primary-fixed: '#311300'
  on-primary-fixed-variant: '#733500'
  secondary-fixed: '#dce1ff'
  secondary-fixed-dim: '#b6c4ff'
  on-secondary-fixed: '#00164f'
  on-secondary-fixed-variant: '#003bb0'
  tertiary-fixed: '#72ff70'
  tertiary-fixed-dim: '#4ee253'
  on-tertiary-fixed: '#002203'
  on-tertiary-fixed-variant: '#00530e'
  background: '#f9f9ff'
  on-background: '#181c23'
  surface-variant: '#dfe2ed'
typography:
  title-xl:
    fontFamily: PingFang SC
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 30px
  text-lg:
    fontFamily: PingFang SC
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  text-md:
    fontFamily: PingFang SC
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-base:
    fontFamily: PingFang SC
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
  label-sm:
    fontFamily: PingFang SC
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  micro-xs:
    fontFamily: PingFang SC
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 15px
  title-xl-mobile:
    fontFamily: PingFang SC
    fontSize: 40rpx
    fontWeight: '700'
    lineHeight: 60rpx
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 40px
---

## Brand & Style

The design system is built on a foundation of **Warm Professionalism**, balancing the high-velocity nature of logistics with the reliability of cloud printing. The aesthetic is defined as **Modern Card-Based**, characterized by soft geometric shapes, a warm-toned neutral palette, and high-contrast interactive elements.

The UI evokes an emotional response of trust and accessibility. By moving away from clinical pure whites toward a "Warm Rice White" foundation, the system reduces eye strain and feels more approachable for daily consumer use. It employs a **Dual-Accent Strategy**:
- **Vitality Orange** signals energy and urgency for the Express Delivery flow.
- **Classic Blue** signals precision and security for the Document Printing flow.

This visual strategy uses heavy whitespace, rounded corners (up to 24rpx on mobile), and soft ambient shadows to create a tactile, layered experience that feels both high-tech and human-centric.

## Colors

The palette is anchored by the **Warm Rice White (#F9F6F0)** background, which serves as a textured canvas for pure white content cards.

- **Primary (Vitality Orange):** Used for the Express Delivery domain, primary call-to-actions, and main brand highlights.
- **Secondary (Classic Blue):** Used for the Document Printing domain and informational highlights to distinguish productivity tasks.
- **Neutral (Slate):** A deep charcoal (#1D2129) is used for high-hierarchy text to ensure maximum legibility against the cream background.
- **Functional Tints:** For status labels and badges, use a 10-15% opacity tint of the functional color for the background, paired with the full-saturation color for the text to maintain a soft, modern appearance.

## Typography

This design system uses a clean, humanist hierarchy optimized for bilingual Chinese and English environments. 

- **Primary Typeface:** PingFang SC for mobile (WeChat Mini Program) and a system sans-serif stack for the management backend.
- **Hierarchy:** Use **Bold/700** for H1 titles and **Semi-Bold/600** for card headers to create clear entry points for the eye.
- **Body & Captions:** Standardize on 14px for body text to ensure readability across all demographics. 11px/12px are reserved strictly for metadata, labels, and tags.
- **Scaling:** On mobile, typography should transition to `rpx` units. Heading sizes remain prominent but must not exceed 42rpx to maintain balance on smaller screens.

## Layout & Spacing

The layout follows a **Fluid Grid** model for mobile and a **Fixed Sidebar + Fluid Content** model for desktop.

- **Spacing Rhythm:** Based on a 4px (8rpx) incremental unit. 
- **Mobile Layout:** 20rpx-30rpx side margins are required. Card elements should have a vertical gap of 20rpx. 
- **Desktop Layout:** Features a fixed 220px left sidebar in deep charcoal (#1D2129). Content is organized into a 12-column grid with 24px gutters.
- **Safe Areas:** On mobile, always include a 160rpx bottom padding to prevent content from being obscured by sticky action bars or navigation menus.

## Elevation & Depth

The design system uses **Tonal Layering** combined with **Ambient Shadows** to create a sense of physical space.

1.  **Base Layer:** The Warm Rice White background (#F9F6F0).
2.  **Surface Layer (Cards):** Pure white containers (#FFFFFF) float on the base.
3.  **Elevation Shadows:** Use extra-diffused shadows.
    - *Card Shadow:* `box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06)`.
    - *Interactive Shadow (Orange):* `box-shadow: 0 4px 24rpx rgba(255,125,0,0.35)` for primary orange buttons.
    - *Interactive Shadow (Blue):* `box-shadow: 0 4px 24rpx rgba(24,144,255,0.3)` for blue printing actions.
4.  **Borders:** Use a subtle 1px border (#E5E6EB) for PC backend components and card-based form fields to maintain professional structure.

## Shapes

The shape language is rounded and friendly, reinforcing the "Warm Professionalism" theme.

- **Cards:** Use a 12px (24rpx) radius as the standard for all content containers.
- **Buttons:** Primary buttons use a **Capsule** shape (999px radius) for high visibility and touch comfort on mobile, or 8px (16rpx) for standard desktop controls.
- **Inputs:** A softer 6px (12rpx) radius is used for input fields to provide a distinct look from larger card containers.
- **Status Tags:** Use a 4px radius for a clean, professional finish.

## Components

### Buttons
- **Primary:** Capsule-shaped with white text. Apply a linear gradient (`#FF7D00` to `#FF9A3C`) for Express Delivery and (`#165DFF` to `#096dd9`) for Printing.
- **Secondary:** White background with `#E5E6EB` border and `#4E5969` text.

### Cards
- **Container:** Pure white, 12px radius, soft shadow.
- **Address Selection:** Pair sender/receiver cards with a central circular floating exchange button.

### Status Labels
- Use "Pill" shapes with 10% opacity backgrounds. Text must be the high-saturation functional color (e.g., Green text on Light Green background for "Completed").

### Inputs
- **Card-based Fields:** 76rpx height on mobile. Light gray background (#F2F3F5) when inactive. Upon focus, the border transitions to the primary color (Orange or Blue) and the background clears to white.

### Specialized Components
- **File Upload:** Dotted border (`2rpx dashed #d9d9d9`) with a `#FAFAFA` background. Center-aligned icons and supportive text.
- **PC Sidebar:** 220px width, `#1D2129` background. Active states use an orange left-border accent (3px width) and a 15% opacity orange backdrop highlight.
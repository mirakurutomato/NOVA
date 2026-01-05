#version 300 es
precision mediump float;

in vec2 vTexCoord;
out vec4 outColor;

uniform samplerExternalOES sTexture;
uniform float uSigma; // Contrast/Curve steepness 'a' in python
uniform float uN;     // Blur radius approximation
uniform float uBrightThresh;
uniform float uBrightK;
uniform float uGamma;

// Simple Box Blur acting as approximation for "Sigmoid Separable Blur" for performance
// In a real production app, we would use a two-pass Gaussian or Separable shader.
// For single-pass simplicity to avoid frame drops on mobile:
vec3 blur(samplerExternalOES tex, vec2 uv, float radius) {
    vec3 col = vec3(0.0);
    // 9-tap approximations
    vec2 off = vec2(radius, 0.0) / 1000.0; // Approximation of pixel size
    col += texture(tex, uv).rgb * 0.2;
    col += texture(tex, uv + off).rgb * 0.1;
    col += texture(tex, uv - off).rgb * 0.1;
    col += texture(tex, uv + off.yx).rgb * 0.1;
    col += texture(tex, uv - off.yx).rgb * 0.1;
    col += texture(tex, uv + off * 2.0).rgb * 0.1;
    col += texture(tex, uv - off * 2.0).rgb * 0.1;
    col += texture(tex, uv + off.yx * 2.0).rgb * 0.1;
    col += texture(tex, uv - off.yx * 2.0).rgb * 0.1;
    return col;
}

void main() {
    vec4 texColor = texture(sTexture, vTexCoord);
    vec3 img = texColor.rgb;

    // 1. Luminance (Y)
    float Y = dot(img, vec3(0.299, 0.587, 0.114));

    // 2. Estimation of Illumination (L) via Blur
    // Matching "sigmoid_separable_blur" roughly
    vec3 L_rgb = blur(sTexture, vTexCoord, uN * 2.0);
    float L = dot(L_rgb, vec3(0.299, 0.587, 0.114));
    L = max(L, 0.001); // Avoid div by zero

    // 3. Reflectance R = Y / L
    float R = Y / L;

    // 4. Scale s(x,y)
    float s = clamp(R, 0.5, 1.5);

    // 5. Highlight protection weight w(L)
    // w = 1 / (1 + exp((L - t)/k))
    float w = 1.0 / (1.0 + exp((L - uBrightThresh) / (uBrightK + 0.00001)));

    // 6. Final Scale S_final = 1 + w*(s - 1)
    float S_final = 1.0 + w * (s - 1.0);

    // 7. Apply to original image
    vec3 outRGB = img * S_final;

    // 8. Gamma Correction
    outRGB = pow(outRGB, vec3(uGamma));

    outColor = vec4(outRGB, 1.0);
}

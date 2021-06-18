#version 330 core

in vec2 coordinate;
in vec3 surfaceNormal;
in vec3 outLight;
in vec3 outCamera;

out vec4 color;

uniform sampler2D textureImage;
uniform vec3 lightColor;
uniform float reflectivity;
uniform float shineAngle;

void main(void) {
	vec3 nLight = normalize(outLight);
	vec3 nNormal = normalize(surfaceNormal);
	vec3 nCamera = normalize(outCamera);
	float lightDot = dot(nNormal, nLight);
	float specular = max(dot(reflect(-nLight, nNormal), nCamera), 0.0);
	float SpecAngled = pow(specular, shineAngle);
	color = vec4(max(lightDot, 0.12) * lightColor, 1.0) * texture(textureImage, coordinate) + vec4(SpecAngled * reflectivity * lightColor, 1.0);
}

#version 150

in ivec4 InstancePosFace;   // xyz + faceId
in ivec4 InstanceData;      // materialId, packedLight, flags, reserved

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord;
flat out int materialId;

const vec3 FACE_VERTS[6][4] = vec3[6][4](
    vec3[4](vec3(0,0,0), vec3(0,1,0), vec3(0,1,1), vec3(0,0,1)),
    vec3[4](vec3(1,0,1), vec3(1,1,1), vec3(1,1,0), vec3(1,0,0)),
    vec3[4](vec3(0,0,1), vec3(1,0,1), vec3(1,0,0), vec3(0,0,0)),
    vec3[4](vec3(0,1,0), vec3(1,1,0), vec3(1,1,1), vec3(0,1,1)),
    vec3[4](vec3(1,0,0), vec3(1,1,0), vec3(0,1,0), vec3(0,0,0)),
    vec3[4](vec3(0,0,1), vec3(0,1,1), vec3(1,1,1), vec3(1,0,1))
);

const vec2 UVS[4] = vec2[4](vec2(0,0), vec2(0,1), vec2(1,1), vec2(1,0));

void main() {
    int local = gl_VertexID & 3;
    int face = InstancePosFace.w;
    vec3 blockPos = vec3(InstancePosFace.xyz);
    vec3 worldPos = blockPos + FACE_VERTS[face][local];

    texCoord = UVS[local];
    materialId = InstanceData.x;
    gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
}

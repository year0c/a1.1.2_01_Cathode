package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.PointerBuffer;

public class GL11 {

    // --- GL11 constants ---
    public static final int GL_NEVER = GL11C.GL_NEVER;
    public static final int GL_LESS = GL11C.GL_LESS;
    public static final int GL_EQUAL = GL11C.GL_EQUAL;
    public static final int GL_LEQUAL = GL11C.GL_LEQUAL;
    public static final int GL_GREATER = GL11C.GL_GREATER;
    public static final int GL_NOTEQUAL = GL11C.GL_NOTEQUAL;
    public static final int GL_GEQUAL = GL11C.GL_GEQUAL;
    public static final int GL_ALWAYS = GL11C.GL_ALWAYS;
    public static final int GL_DEPTH_BUFFER_BIT = GL11C.GL_DEPTH_BUFFER_BIT;
    public static final int GL_STENCIL_BUFFER_BIT = GL11C.GL_STENCIL_BUFFER_BIT;
    public static final int GL_COLOR_BUFFER_BIT = GL11C.GL_COLOR_BUFFER_BIT;
    public static final int GL_POINTS = GL11C.GL_POINTS;
    public static final int GL_LINES = GL11C.GL_LINES;
    public static final int GL_LINE_LOOP = GL11C.GL_LINE_LOOP;
    public static final int GL_LINE_STRIP = GL11C.GL_LINE_STRIP;
    public static final int GL_TRIANGLES = GL11C.GL_TRIANGLES;
    public static final int GL_TRIANGLE_STRIP = GL11C.GL_TRIANGLE_STRIP;
    public static final int GL_TRIANGLE_FAN = GL11C.GL_TRIANGLE_FAN;
    public static final int GL_QUADS = GL11C.GL_QUADS;
    public static final int GL_ZERO = GL11C.GL_ZERO;
    public static final int GL_ONE = GL11C.GL_ONE;
    public static final int GL_SRC_COLOR = GL11C.GL_SRC_COLOR;
    public static final int GL_ONE_MINUS_SRC_COLOR = GL11C.GL_ONE_MINUS_SRC_COLOR;
    public static final int GL_SRC_ALPHA = GL11C.GL_SRC_ALPHA;
    public static final int GL_ONE_MINUS_SRC_ALPHA = GL11C.GL_ONE_MINUS_SRC_ALPHA;
    public static final int GL_DST_ALPHA = GL11C.GL_DST_ALPHA;
    public static final int GL_ONE_MINUS_DST_ALPHA = GL11C.GL_ONE_MINUS_DST_ALPHA;
    public static final int GL_DST_COLOR = GL11C.GL_DST_COLOR;
    public static final int GL_ONE_MINUS_DST_COLOR = GL11C.GL_ONE_MINUS_DST_COLOR;
    public static final int GL_SRC_ALPHA_SATURATE = GL11C.GL_SRC_ALPHA_SATURATE;
    public static final int GL_TRUE = GL11C.GL_TRUE;
    public static final int GL_FALSE = GL11C.GL_FALSE;
    public static final int GL_BYTE = GL11C.GL_BYTE;
    public static final int GL_UNSIGNED_BYTE = GL11C.GL_UNSIGNED_BYTE;
    public static final int GL_SHORT = GL11C.GL_SHORT;
    public static final int GL_UNSIGNED_SHORT = GL11C.GL_UNSIGNED_SHORT;
    public static final int GL_INT = GL11C.GL_INT;
    public static final int GL_UNSIGNED_INT = GL11C.GL_UNSIGNED_INT;
    public static final int GL_FLOAT = GL11C.GL_FLOAT;
    public static final int GL_DOUBLE = GL11C.GL_DOUBLE;
    public static final int GL_NONE = GL11C.GL_NONE;
    public static final int GL_FRONT_LEFT = GL11C.GL_FRONT_LEFT;
    public static final int GL_FRONT_RIGHT = GL11C.GL_FRONT_RIGHT;
    public static final int GL_BACK_LEFT = GL11C.GL_BACK_LEFT;
    public static final int GL_BACK_RIGHT = GL11C.GL_BACK_RIGHT;
    public static final int GL_FRONT = GL11C.GL_FRONT;
    public static final int GL_BACK = GL11C.GL_BACK;
    public static final int GL_LEFT = GL11C.GL_LEFT;
    public static final int GL_RIGHT = GL11C.GL_RIGHT;
    public static final int GL_FRONT_AND_BACK = GL11C.GL_FRONT_AND_BACK;
    public static final int GL_NO_ERROR = GL11C.GL_NO_ERROR;
    public static final int GL_INVALID_ENUM = GL11C.GL_INVALID_ENUM;
    public static final int GL_INVALID_VALUE = GL11C.GL_INVALID_VALUE;
    public static final int GL_INVALID_OPERATION = GL11C.GL_INVALID_OPERATION;
    public static final int GL_STACK_OVERFLOW = GL11C.GL_STACK_OVERFLOW;
    public static final int GL_STACK_UNDERFLOW = GL11C.GL_STACK_UNDERFLOW;
    public static final int GL_OUT_OF_MEMORY = GL11C.GL_OUT_OF_MEMORY;
    public static final int GL_CW = GL11C.GL_CW;
    public static final int GL_CCW = GL11C.GL_CCW;
    public static final int GL_POINT_SIZE = GL11C.GL_POINT_SIZE;
    public static final int GL_POINT_SIZE_RANGE = GL11C.GL_POINT_SIZE_RANGE;
    public static final int GL_POINT_SIZE_GRANULARITY = GL11C.GL_POINT_SIZE_GRANULARITY;
    public static final int GL_LINE_SMOOTH = GL11C.GL_LINE_SMOOTH;
    public static final int GL_LINE_WIDTH = GL11C.GL_LINE_WIDTH;
    public static final int GL_LINE_WIDTH_RANGE = GL11C.GL_LINE_WIDTH_RANGE;
    public static final int GL_LINE_WIDTH_GRANULARITY = GL11C.GL_LINE_WIDTH_GRANULARITY;
    public static final int GL_POLYGON_MODE = GL11C.GL_POLYGON_MODE;
    public static final int GL_POLYGON_SMOOTH = GL11C.GL_POLYGON_SMOOTH;
    public static final int GL_CULL_FACE = GL11C.GL_CULL_FACE;
    public static final int GL_CULL_FACE_MODE = GL11C.GL_CULL_FACE_MODE;
    public static final int GL_FRONT_FACE = GL11C.GL_FRONT_FACE;
    public static final int GL_DEPTH_RANGE = GL11C.GL_DEPTH_RANGE;
    public static final int GL_DEPTH_TEST = GL11C.GL_DEPTH_TEST;
    public static final int GL_DEPTH_WRITEMASK = GL11C.GL_DEPTH_WRITEMASK;
    public static final int GL_DEPTH_CLEAR_VALUE = GL11C.GL_DEPTH_CLEAR_VALUE;
    public static final int GL_DEPTH_FUNC = GL11C.GL_DEPTH_FUNC;
    public static final int GL_STENCIL_TEST = GL11C.GL_STENCIL_TEST;
    public static final int GL_STENCIL_CLEAR_VALUE = GL11C.GL_STENCIL_CLEAR_VALUE;
    public static final int GL_STENCIL_FUNC = GL11C.GL_STENCIL_FUNC;
    public static final int GL_STENCIL_VALUE_MASK = GL11C.GL_STENCIL_VALUE_MASK;
    public static final int GL_STENCIL_FAIL = GL11C.GL_STENCIL_FAIL;
    public static final int GL_STENCIL_PASS_DEPTH_FAIL = GL11C.GL_STENCIL_PASS_DEPTH_FAIL;
    public static final int GL_STENCIL_PASS_DEPTH_PASS = GL11C.GL_STENCIL_PASS_DEPTH_PASS;
    public static final int GL_STENCIL_REF = GL11C.GL_STENCIL_REF;
    public static final int GL_STENCIL_WRITEMASK = GL11C.GL_STENCIL_WRITEMASK;
    public static final int GL_VIEWPORT = GL11C.GL_VIEWPORT;
    public static final int GL_DITHER = GL11C.GL_DITHER;
    public static final int GL_BLEND_DST = GL11C.GL_BLEND_DST;
    public static final int GL_BLEND_SRC = GL11C.GL_BLEND_SRC;
    public static final int GL_BLEND = GL11C.GL_BLEND;
    public static final int GL_LOGIC_OP_MODE = GL11C.GL_LOGIC_OP_MODE;
    public static final int GL_COLOR_LOGIC_OP = GL11C.GL_COLOR_LOGIC_OP;
    public static final int GL_DRAW_BUFFER = GL11C.GL_DRAW_BUFFER;
    public static final int GL_READ_BUFFER = GL11C.GL_READ_BUFFER;
    public static final int GL_SCISSOR_BOX = GL11C.GL_SCISSOR_BOX;
    public static final int GL_SCISSOR_TEST = GL11C.GL_SCISSOR_TEST;
    public static final int GL_COLOR_CLEAR_VALUE = GL11C.GL_COLOR_CLEAR_VALUE;
    public static final int GL_COLOR_WRITEMASK = GL11C.GL_COLOR_WRITEMASK;
    public static final int GL_DOUBLEBUFFER = GL11C.GL_DOUBLEBUFFER;
    public static final int GL_STEREO = GL11C.GL_STEREO;
    public static final int GL_LINE_SMOOTH_HINT = GL11C.GL_LINE_SMOOTH_HINT;
    public static final int GL_POLYGON_SMOOTH_HINT = GL11C.GL_POLYGON_SMOOTH_HINT;
    public static final int GL_UNPACK_SWAP_BYTES = GL11C.GL_UNPACK_SWAP_BYTES;
    public static final int GL_UNPACK_LSB_FIRST = GL11C.GL_UNPACK_LSB_FIRST;
    public static final int GL_UNPACK_ROW_LENGTH = GL11C.GL_UNPACK_ROW_LENGTH;
    public static final int GL_UNPACK_SKIP_ROWS = GL11C.GL_UNPACK_SKIP_ROWS;
    public static final int GL_UNPACK_SKIP_PIXELS = GL11C.GL_UNPACK_SKIP_PIXELS;
    public static final int GL_UNPACK_ALIGNMENT = GL11C.GL_UNPACK_ALIGNMENT;
    public static final int GL_PACK_SWAP_BYTES = GL11C.GL_PACK_SWAP_BYTES;
    public static final int GL_PACK_LSB_FIRST = GL11C.GL_PACK_LSB_FIRST;
    public static final int GL_PACK_ROW_LENGTH = GL11C.GL_PACK_ROW_LENGTH;
    public static final int GL_PACK_SKIP_ROWS = GL11C.GL_PACK_SKIP_ROWS;
    public static final int GL_PACK_SKIP_PIXELS = GL11C.GL_PACK_SKIP_PIXELS;
    public static final int GL_PACK_ALIGNMENT = GL11C.GL_PACK_ALIGNMENT;
    public static final int GL_MAX_TEXTURE_SIZE = GL11C.GL_MAX_TEXTURE_SIZE;
    public static final int GL_MAX_VIEWPORT_DIMS = GL11C.GL_MAX_VIEWPORT_DIMS;
    public static final int GL_SUBPIXEL_BITS = GL11C.GL_SUBPIXEL_BITS;
    public static final int GL_TEXTURE_1D = GL11C.GL_TEXTURE_1D;
    public static final int GL_TEXTURE_2D = GL11C.GL_TEXTURE_2D;
    public static final int GL_TEXTURE_WIDTH = GL11C.GL_TEXTURE_WIDTH;
    public static final int GL_TEXTURE_HEIGHT = GL11C.GL_TEXTURE_HEIGHT;
    public static final int GL_TEXTURE_INTERNAL_FORMAT = GL11C.GL_TEXTURE_INTERNAL_FORMAT;
    public static final int GL_TEXTURE_BORDER_COLOR = GL11C.GL_TEXTURE_BORDER_COLOR;
    public static final int GL_DONT_CARE = GL11C.GL_DONT_CARE;
    public static final int GL_FASTEST = GL11C.GL_FASTEST;
    public static final int GL_NICEST = GL11C.GL_NICEST;
    public static final int GL_CLEAR = GL11C.GL_CLEAR;
    public static final int GL_AND = GL11C.GL_AND;
    public static final int GL_AND_REVERSE = GL11C.GL_AND_REVERSE;
    public static final int GL_COPY = GL11C.GL_COPY;
    public static final int GL_AND_INVERTED = GL11C.GL_AND_INVERTED;
    public static final int GL_NOOP = GL11C.GL_NOOP;
    public static final int GL_XOR = GL11C.GL_XOR;
    public static final int GL_OR = GL11C.GL_OR;
    public static final int GL_NOR = GL11C.GL_NOR;
    public static final int GL_EQUIV = GL11C.GL_EQUIV;
    public static final int GL_INVERT = GL11C.GL_INVERT;
    public static final int GL_OR_REVERSE = GL11C.GL_OR_REVERSE;
    public static final int GL_COPY_INVERTED = GL11C.GL_COPY_INVERTED;
    public static final int GL_OR_INVERTED = GL11C.GL_OR_INVERTED;
    public static final int GL_NAND = GL11C.GL_NAND;
    public static final int GL_SET = GL11C.GL_SET;
    public static final int GL_TEXTURE = GL11C.GL_TEXTURE;
    public static final int GL_COLOR = GL11C.GL_COLOR;
    public static final int GL_DEPTH = GL11C.GL_DEPTH;
    public static final int GL_STENCIL = GL11C.GL_STENCIL;
    public static final int GL_STENCIL_INDEX = GL11C.GL_STENCIL_INDEX;
    public static final int GL_DEPTH_COMPONENT = GL11C.GL_DEPTH_COMPONENT;
    public static final int GL_RED = GL11C.GL_RED;
    public static final int GL_GREEN = GL11C.GL_GREEN;
    public static final int GL_BLUE = GL11C.GL_BLUE;
    public static final int GL_ALPHA = GL11C.GL_ALPHA;
    public static final int GL_RGB = GL11C.GL_RGB;
    public static final int GL_RGBA = GL11C.GL_RGBA;
    public static final int GL_POINT = GL11C.GL_POINT;
    public static final int GL_LINE = GL11C.GL_LINE;
    public static final int GL_FILL = GL11C.GL_FILL;
    public static final int GL_KEEP = GL11C.GL_KEEP;
    public static final int GL_REPLACE = GL11C.GL_REPLACE;
    public static final int GL_INCR = GL11C.GL_INCR;
    public static final int GL_DECR = GL11C.GL_DECR;
    public static final int GL_VENDOR = GL11C.GL_VENDOR;
    public static final int GL_RENDERER = GL11C.GL_RENDERER;
    public static final int GL_VERSION = GL11C.GL_VERSION;
    public static final int GL_EXTENSIONS = GL11C.GL_EXTENSIONS;
    public static final int GL_NEAREST = GL11C.GL_NEAREST;
    public static final int GL_LINEAR = GL11C.GL_LINEAR;
    public static final int GL_NEAREST_MIPMAP_NEAREST = GL11C.GL_NEAREST_MIPMAP_NEAREST;
    public static final int GL_LINEAR_MIPMAP_NEAREST = GL11C.GL_LINEAR_MIPMAP_NEAREST;
    public static final int GL_NEAREST_MIPMAP_LINEAR = GL11C.GL_NEAREST_MIPMAP_LINEAR;
    public static final int GL_LINEAR_MIPMAP_LINEAR = GL11C.GL_LINEAR_MIPMAP_LINEAR;
    public static final int GL_TEXTURE_MAG_FILTER = GL11C.GL_TEXTURE_MAG_FILTER;
    public static final int GL_TEXTURE_MIN_FILTER = GL11C.GL_TEXTURE_MIN_FILTER;
    public static final int GL_TEXTURE_WRAP_S = GL11C.GL_TEXTURE_WRAP_S;
    public static final int GL_TEXTURE_WRAP_T = GL11C.GL_TEXTURE_WRAP_T;
    public static final int GL_REPEAT = GL11C.GL_REPEAT;
    public static final int GL_POLYGON_OFFSET_FACTOR = GL11C.GL_POLYGON_OFFSET_FACTOR;
    public static final int GL_POLYGON_OFFSET_UNITS = GL11C.GL_POLYGON_OFFSET_UNITS;
    public static final int GL_POLYGON_OFFSET_POINT = GL11C.GL_POLYGON_OFFSET_POINT;
    public static final int GL_POLYGON_OFFSET_LINE = GL11C.GL_POLYGON_OFFSET_LINE;
    public static final int GL_POLYGON_OFFSET_FILL = GL11C.GL_POLYGON_OFFSET_FILL;
    public static final int GL_R3_G3_B2 = GL11C.GL_R3_G3_B2;
    public static final int GL_RGB4 = GL11C.GL_RGB4;
    public static final int GL_RGB5 = GL11C.GL_RGB5;
    public static final int GL_RGB8 = GL11C.GL_RGB8;
    public static final int GL_RGB10 = GL11C.GL_RGB10;
    public static final int GL_RGB12 = GL11C.GL_RGB12;
    public static final int GL_RGB16 = GL11C.GL_RGB16;
    public static final int GL_RGBA2 = GL11C.GL_RGBA2;
    public static final int GL_RGBA4 = GL11C.GL_RGBA4;
    public static final int GL_RGB5_A1 = GL11C.GL_RGB5_A1;
    public static final int GL_RGBA8 = GL11C.GL_RGBA8;
    public static final int GL_RGB10_A2 = GL11C.GL_RGB10_A2;
    public static final int GL_RGBA12 = GL11C.GL_RGBA12;
    public static final int GL_RGBA16 = GL11C.GL_RGBA16;
    public static final int GL_TEXTURE_RED_SIZE = GL11C.GL_TEXTURE_RED_SIZE;
    public static final int GL_TEXTURE_GREEN_SIZE = GL11C.GL_TEXTURE_GREEN_SIZE;
    public static final int GL_TEXTURE_BLUE_SIZE = GL11C.GL_TEXTURE_BLUE_SIZE;
    public static final int GL_TEXTURE_ALPHA_SIZE = GL11C.GL_TEXTURE_ALPHA_SIZE;
    public static final int GL_PROXY_TEXTURE_1D = GL11C.GL_PROXY_TEXTURE_1D;
    public static final int GL_PROXY_TEXTURE_2D = GL11C.GL_PROXY_TEXTURE_2D;
    public static final int GL_TEXTURE_BINDING_1D = GL11C.GL_TEXTURE_BINDING_1D;
    public static final int GL_TEXTURE_BINDING_2D = GL11C.GL_TEXTURE_BINDING_2D;
    public static final int GL_VERTEX_ARRAY = GL11C.GL_VERTEX_ARRAY;

    // --- GL12 constants ---
    public static final int GL_ALIASED_LINE_WIDTH_RANGE = GL12C.GL_ALIASED_LINE_WIDTH_RANGE;
    public static final int GL_SMOOTH_POINT_SIZE_RANGE = GL12C.GL_SMOOTH_POINT_SIZE_RANGE;
    public static final int GL_SMOOTH_POINT_SIZE_GRANULARITY = GL12C.GL_SMOOTH_POINT_SIZE_GRANULARITY;
    public static final int GL_SMOOTH_LINE_WIDTH_RANGE = GL12C.GL_SMOOTH_LINE_WIDTH_RANGE;
    public static final int GL_SMOOTH_LINE_WIDTH_GRANULARITY = GL12C.GL_SMOOTH_LINE_WIDTH_GRANULARITY;
    public static final int GL_TEXTURE_BINDING_3D = GL12C.GL_TEXTURE_BINDING_3D;
    public static final int GL_PACK_SKIP_IMAGES = GL12C.GL_PACK_SKIP_IMAGES;
    public static final int GL_PACK_IMAGE_HEIGHT = GL12C.GL_PACK_IMAGE_HEIGHT;
    public static final int GL_UNPACK_SKIP_IMAGES = GL12C.GL_UNPACK_SKIP_IMAGES;
    public static final int GL_UNPACK_IMAGE_HEIGHT = GL12C.GL_UNPACK_IMAGE_HEIGHT;
    public static final int GL_TEXTURE_3D = GL12C.GL_TEXTURE_3D;
    public static final int GL_PROXY_TEXTURE_3D = GL12C.GL_PROXY_TEXTURE_3D;
    public static final int GL_TEXTURE_DEPTH = GL12C.GL_TEXTURE_DEPTH;
    public static final int GL_TEXTURE_WRAP_R = GL12C.GL_TEXTURE_WRAP_R;
    public static final int GL_MAX_3D_TEXTURE_SIZE = GL12C.GL_MAX_3D_TEXTURE_SIZE;
    public static final int GL_BGR = GL12C.GL_BGR;
    public static final int GL_BGRA = GL12C.GL_BGRA;
    public static final int GL_UNSIGNED_BYTE_3_3_2 = GL12C.GL_UNSIGNED_BYTE_3_3_2;
    public static final int GL_UNSIGNED_BYTE_2_3_3_REV = GL12C.GL_UNSIGNED_BYTE_2_3_3_REV;
    public static final int GL_UNSIGNED_SHORT_5_6_5 = GL12C.GL_UNSIGNED_SHORT_5_6_5;
    public static final int GL_UNSIGNED_SHORT_5_6_5_REV = GL12C.GL_UNSIGNED_SHORT_5_6_5_REV;
    public static final int GL_UNSIGNED_SHORT_4_4_4_4 = GL12C.GL_UNSIGNED_SHORT_4_4_4_4;
    public static final int GL_UNSIGNED_SHORT_4_4_4_4_REV = GL12C.GL_UNSIGNED_SHORT_4_4_4_4_REV;
    public static final int GL_UNSIGNED_SHORT_5_5_5_1 = GL12C.GL_UNSIGNED_SHORT_5_5_5_1;
    public static final int GL_UNSIGNED_SHORT_1_5_5_5_REV = GL12C.GL_UNSIGNED_SHORT_1_5_5_5_REV;
    public static final int GL_UNSIGNED_INT_8_8_8_8 = GL12C.GL_UNSIGNED_INT_8_8_8_8;
    public static final int GL_UNSIGNED_INT_8_8_8_8_REV = GL12C.GL_UNSIGNED_INT_8_8_8_8_REV;
    public static final int GL_UNSIGNED_INT_10_10_10_2 = GL12C.GL_UNSIGNED_INT_10_10_10_2;
    public static final int GL_UNSIGNED_INT_2_10_10_10_REV = GL12C.GL_UNSIGNED_INT_2_10_10_10_REV;
    public static final int GL_CLAMP_TO_EDGE = GL12C.GL_CLAMP_TO_EDGE;
    public static final int GL_TEXTURE_MIN_LOD = GL12C.GL_TEXTURE_MIN_LOD;
    public static final int GL_TEXTURE_MAX_LOD = GL12C.GL_TEXTURE_MAX_LOD;
    public static final int GL_TEXTURE_BASE_LEVEL = GL12C.GL_TEXTURE_BASE_LEVEL;
    public static final int GL_TEXTURE_MAX_LEVEL = GL12C.GL_TEXTURE_MAX_LEVEL;
    public static final int GL_MAX_ELEMENTS_VERTICES = GL12C.GL_MAX_ELEMENTS_VERTICES;
    public static final int GL_MAX_ELEMENTS_INDICES = GL12C.GL_MAX_ELEMENTS_INDICES;

    // --- GL13 constants ---
    public static final int GL_COMPRESSED_RGB = GL13C.GL_COMPRESSED_RGB;
    public static final int GL_COMPRESSED_RGBA = GL13C.GL_COMPRESSED_RGBA;
    public static final int GL_TEXTURE_COMPRESSION_HINT = GL13C.GL_TEXTURE_COMPRESSION_HINT;
    public static final int GL_TEXTURE_COMPRESSED_IMAGE_SIZE = GL13C.GL_TEXTURE_COMPRESSED_IMAGE_SIZE;
    public static final int GL_TEXTURE_COMPRESSED = GL13C.GL_TEXTURE_COMPRESSED;
    public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = GL13C.GL_NUM_COMPRESSED_TEXTURE_FORMATS;
    public static final int GL_COMPRESSED_TEXTURE_FORMATS = GL13C.GL_COMPRESSED_TEXTURE_FORMATS;
    public static final int GL_TEXTURE_CUBE_MAP = GL13C.GL_TEXTURE_CUBE_MAP;
    public static final int GL_TEXTURE_BINDING_CUBE_MAP = GL13C.GL_TEXTURE_BINDING_CUBE_MAP;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
    public static final int GL_PROXY_TEXTURE_CUBE_MAP = GL13C.GL_PROXY_TEXTURE_CUBE_MAP;
    public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = GL13C.GL_MAX_CUBE_MAP_TEXTURE_SIZE;
    public static final int GL_MULTISAMPLE = GL13C.GL_MULTISAMPLE;
    public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = GL13C.GL_SAMPLE_ALPHA_TO_COVERAGE;
    public static final int GL_SAMPLE_ALPHA_TO_ONE = GL13C.GL_SAMPLE_ALPHA_TO_ONE;
    public static final int GL_SAMPLE_COVERAGE = GL13C.GL_SAMPLE_COVERAGE;
    public static final int GL_SAMPLE_BUFFERS = GL13C.GL_SAMPLE_BUFFERS;
    public static final int GL_SAMPLES = GL13C.GL_SAMPLES;
    public static final int GL_SAMPLE_COVERAGE_VALUE = GL13C.GL_SAMPLE_COVERAGE_VALUE;
    public static final int GL_SAMPLE_COVERAGE_INVERT = GL13C.GL_SAMPLE_COVERAGE_INVERT;
    public static final int GL_TEXTURE0 = GL13C.GL_TEXTURE0;
    public static final int GL_TEXTURE1 = GL13C.GL_TEXTURE1;
    public static final int GL_TEXTURE2 = GL13C.GL_TEXTURE2;
    public static final int GL_TEXTURE3 = GL13C.GL_TEXTURE3;
    public static final int GL_TEXTURE4 = GL13C.GL_TEXTURE4;
    public static final int GL_TEXTURE5 = GL13C.GL_TEXTURE5;
    public static final int GL_TEXTURE6 = GL13C.GL_TEXTURE6;
    public static final int GL_TEXTURE7 = GL13C.GL_TEXTURE7;
    public static final int GL_TEXTURE8 = GL13C.GL_TEXTURE8;
    public static final int GL_TEXTURE9 = GL13C.GL_TEXTURE9;
    public static final int GL_TEXTURE10 = GL13C.GL_TEXTURE10;
    public static final int GL_TEXTURE11 = GL13C.GL_TEXTURE11;
    public static final int GL_TEXTURE12 = GL13C.GL_TEXTURE12;
    public static final int GL_TEXTURE13 = GL13C.GL_TEXTURE13;
    public static final int GL_TEXTURE14 = GL13C.GL_TEXTURE14;
    public static final int GL_TEXTURE15 = GL13C.GL_TEXTURE15;
    public static final int GL_TEXTURE16 = GL13C.GL_TEXTURE16;
    public static final int GL_TEXTURE17 = GL13C.GL_TEXTURE17;
    public static final int GL_TEXTURE18 = GL13C.GL_TEXTURE18;
    public static final int GL_TEXTURE19 = GL13C.GL_TEXTURE19;
    public static final int GL_TEXTURE20 = GL13C.GL_TEXTURE20;
    public static final int GL_TEXTURE21 = GL13C.GL_TEXTURE21;
    public static final int GL_TEXTURE22 = GL13C.GL_TEXTURE22;
    public static final int GL_TEXTURE23 = GL13C.GL_TEXTURE23;
    public static final int GL_TEXTURE24 = GL13C.GL_TEXTURE24;
    public static final int GL_TEXTURE25 = GL13C.GL_TEXTURE25;
    public static final int GL_TEXTURE26 = GL13C.GL_TEXTURE26;
    public static final int GL_TEXTURE27 = GL13C.GL_TEXTURE27;
    public static final int GL_TEXTURE28 = GL13C.GL_TEXTURE28;
    public static final int GL_TEXTURE29 = GL13C.GL_TEXTURE29;
    public static final int GL_TEXTURE30 = GL13C.GL_TEXTURE30;
    public static final int GL_TEXTURE31 = GL13C.GL_TEXTURE31;
    public static final int GL_ACTIVE_TEXTURE = GL13C.GL_ACTIVE_TEXTURE;
    public static final int GL_CLAMP_TO_BORDER = GL13C.GL_CLAMP_TO_BORDER;

    // --- GL14 constants ---
    public static final int GL_CONSTANT_COLOR = GL14C.GL_CONSTANT_COLOR;
    public static final int GL_ONE_MINUS_CONSTANT_COLOR = GL14C.GL_ONE_MINUS_CONSTANT_COLOR;
    public static final int GL_CONSTANT_ALPHA = GL14C.GL_CONSTANT_ALPHA;
    public static final int GL_ONE_MINUS_CONSTANT_ALPHA = GL14C.GL_ONE_MINUS_CONSTANT_ALPHA;
    public static final int GL_BLEND_COLOR = GL14C.GL_BLEND_COLOR;
    public static final int GL_FUNC_ADD = GL14C.GL_FUNC_ADD;
    public static final int GL_MIN = GL14C.GL_MIN;
    public static final int GL_MAX = GL14C.GL_MAX;
    public static final int GL_BLEND_EQUATION = GL14C.GL_BLEND_EQUATION;
    public static final int GL_FUNC_SUBTRACT = GL14C.GL_FUNC_SUBTRACT;
    public static final int GL_FUNC_REVERSE_SUBTRACT = GL14C.GL_FUNC_REVERSE_SUBTRACT;
    public static final int GL_DEPTH_COMPONENT16 = GL14C.GL_DEPTH_COMPONENT16;
    public static final int GL_DEPTH_COMPONENT24 = GL14C.GL_DEPTH_COMPONENT24;
    public static final int GL_DEPTH_COMPONENT32 = GL14C.GL_DEPTH_COMPONENT32;
    public static final int GL_TEXTURE_DEPTH_SIZE = GL14C.GL_TEXTURE_DEPTH_SIZE;
    public static final int GL_TEXTURE_COMPARE_MODE = GL14C.GL_TEXTURE_COMPARE_MODE;
    public static final int GL_TEXTURE_COMPARE_FUNC = GL14C.GL_TEXTURE_COMPARE_FUNC;
    public static final int GL_POINT_FADE_THRESHOLD_SIZE = GL14C.GL_POINT_FADE_THRESHOLD_SIZE;
    public static final int GL_BLEND_DST_RGB = GL14C.GL_BLEND_DST_RGB;
    public static final int GL_BLEND_SRC_RGB = GL14C.GL_BLEND_SRC_RGB;
    public static final int GL_BLEND_DST_ALPHA = GL14C.GL_BLEND_DST_ALPHA;
    public static final int GL_BLEND_SRC_ALPHA = GL14C.GL_BLEND_SRC_ALPHA;
    public static final int GL_INCR_WRAP = GL14C.GL_INCR_WRAP;
    public static final int GL_DECR_WRAP = GL14C.GL_DECR_WRAP;
    public static final int GL_TEXTURE_LOD_BIAS = GL14C.GL_TEXTURE_LOD_BIAS;
    public static final int GL_MAX_TEXTURE_LOD_BIAS = GL14C.GL_MAX_TEXTURE_LOD_BIAS;
    public static final int GL_MIRRORED_REPEAT = GL14C.GL_MIRRORED_REPEAT;

    // --- GL15 constants ---
    public static final int GL_SRC1_ALPHA = GL15C.GL_SRC1_ALPHA;
    public static final int GL_ARRAY_BUFFER = GL15C.GL_ARRAY_BUFFER;
    public static final int GL_ELEMENT_ARRAY_BUFFER = GL15C.GL_ELEMENT_ARRAY_BUFFER;
    public static final int GL_ARRAY_BUFFER_BINDING = GL15C.GL_ARRAY_BUFFER_BINDING;
    public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = GL15C.GL_ELEMENT_ARRAY_BUFFER_BINDING;
    public static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = GL15C.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
    public static final int GL_STREAM_DRAW = GL15C.GL_STREAM_DRAW;
    public static final int GL_STREAM_READ = GL15C.GL_STREAM_READ;
    public static final int GL_STREAM_COPY = GL15C.GL_STREAM_COPY;
    public static final int GL_STATIC_DRAW = GL15C.GL_STATIC_DRAW;
    public static final int GL_STATIC_READ = GL15C.GL_STATIC_READ;
    public static final int GL_STATIC_COPY = GL15C.GL_STATIC_COPY;
    public static final int GL_DYNAMIC_DRAW = GL15C.GL_DYNAMIC_DRAW;
    public static final int GL_DYNAMIC_READ = GL15C.GL_DYNAMIC_READ;
    public static final int GL_DYNAMIC_COPY = GL15C.GL_DYNAMIC_COPY;
    public static final int GL_READ_ONLY = GL15C.GL_READ_ONLY;
    public static final int GL_WRITE_ONLY = GL15C.GL_WRITE_ONLY;
    public static final int GL_READ_WRITE = GL15C.GL_READ_WRITE;
    public static final int GL_BUFFER_SIZE = GL15C.GL_BUFFER_SIZE;
    public static final int GL_BUFFER_USAGE = GL15C.GL_BUFFER_USAGE;
    public static final int GL_BUFFER_ACCESS = GL15C.GL_BUFFER_ACCESS;
    public static final int GL_BUFFER_MAPPED = GL15C.GL_BUFFER_MAPPED;
    public static final int GL_BUFFER_MAP_POINTER = GL15C.GL_BUFFER_MAP_POINTER;
    public static final int GL_SAMPLES_PASSED = GL15C.GL_SAMPLES_PASSED;
    public static final int GL_QUERY_COUNTER_BITS = GL15C.GL_QUERY_COUNTER_BITS;
    public static final int GL_CURRENT_QUERY = GL15C.GL_CURRENT_QUERY;
    public static final int GL_QUERY_RESULT = GL15C.GL_QUERY_RESULT;
    public static final int GL_QUERY_RESULT_AVAILABLE = GL15C.GL_QUERY_RESULT_AVAILABLE;

    // --- Methods ---
    public static void glDisable(int cap) { GL11C.glDisable(cap); }
    public static boolean glIsEnabled(int cap) { return GL11C.glIsEnabled(cap); }
    public static void glHint(int target, int mode) { GL11C.glHint(target, mode); }
    public static void glLogicOp(int opcode) { GL11C.glLogicOp(opcode); }
    public static void glClear(int mask) { GL11C.glClear(mask); }
    public static void glClearColor(float red, float green, float blue, float alpha) { GL11C.glClearColor(red, green, blue, alpha); }
    public static void glClearDepth(double depth) { GL11C.glClearDepth(depth); }
    public static void glClearStencil(int s) { GL11C.glClearStencil(s); }
    public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) { GL11C.glColorMask(red, green, blue, alpha); }
    public static void glCullFace(int mode) { GL11C.glCullFace(mode); }
    public static void glFrontFace(int mode) { GL11C.glFrontFace(mode); }
    public static void glDepthFunc(int func) { GL11C.glDepthFunc(func); }
    public static void glDepthMask(boolean flag) { GL11C.glDepthMask(flag); }
    public static void glDepthRange(double zNear, double zFar) { GL11C.glDepthRange(zNear, zFar); }
    public static void glDrawArrays(int mode, int first, int count) { GL11C.glDrawArrays(mode, first, count); }
    public static void glDrawBuffer(int mode) { GL11C.glDrawBuffer(mode); }
    public static void glDrawElements(int mode, int count, int type, Buffer indices) { GL11C.glDrawElements(mode, count, type, indices); }
    public static void glDrawElements(int mode, int count, int type, ByteBuffer indices) { GL11C.glDrawElements(mode, count, type, indices); }
    public static void glDrawElements(int mode, int count, int type, ShortBuffer indices) { GL11C.glDrawElements(mode, count, type, indices); }
    public static void glDrawElements(int mode, int count, int type, IntBuffer indices) { GL11C.glDrawElements(mode, count, type, indices); }
    public static void glDrawElements(int mode, int count, int type, long indices) { GL11C.glDrawElements(mode, count, type, indices); }
    public static void glFinish() { GL11C.glFinish(); }
    public static void glFlush() { GL11C.glFlush(); }
    public static void glGenTextures(IntBuffer textures) { GL11C.glGenTextures(textures); }
    public static int glGenTextures() { return GL11C.glGenTextures(); }
    public static void glDeleteTextures(IntBuffer textures) { GL11C.glDeleteTextures(textures); }
    public static void glDeleteTextures(int texture) { GL11C.glDeleteTextures(texture); }
    public static void glBindTexture(int target, int texture) { GL11C.glBindTexture(target, texture); }
    public static boolean glIsTexture(int texture) { return GL11C.glIsTexture(texture); }
    public static void glGetBooleanv(int pname, ByteBuffer data) { GL11C.glGetBooleanv(pname, data); }
    public static boolean glGetBoolean(int pname) { return GL11C.glGetBoolean(pname); }
    public static void glGetFloatv(int pname, FloatBuffer data) { GL11C.glGetFloatv(pname, data); }
    public static float glGetFloat(int pname) { return GL11C.glGetFloat(pname); }
    public static void glGetIntegerv(int pname, IntBuffer data) { GL11C.glGetIntegerv(pname, data); }
    public static int glGetInteger(int pname) { return GL11C.glGetInt(pname); }
    public static void glGetDoublev(int pname, DoubleBuffer data) { GL11C.glGetDoublev(pname, data); }
    public static double glGetDouble(int pname) { return GL11C.glGetDouble(pname); }
    public static int glGetError() { return GL11C.glGetError(); }
    public static void glGetPointerv(int pname, PointerBuffer data) { GL11C.glGetPointerv(pname, data); }
    public static long glGetPointer(int pname) { return GL11C.glGetPointer(pname); }
    public static String glGetString(int name) { return GL11C.glGetString(name); }
    public static void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexImage(int target, int level, int format, int type, ShortBuffer pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexImage(int target, int level, int format, int type, IntBuffer pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexImage(int target, int level, int format, int type, FloatBuffer pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexImage(int target, int level, int format, int type, DoubleBuffer pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexImage(int target, int level, int format, int type, long pixels) { GL11C.glGetTexImage(target, level, format, type, pixels); }
    public static void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) { GL11C.glGetTexLevelParameteriv(target, level, pname, params); }
    public static int glGetTexLevelParameteri(int target, int level, int pname) { return GL11C.glGetTexLevelParameteri(target, level, pname); }
    public static void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) { GL11C.glGetTexLevelParameterfv(target, level, pname, params); }
    public static float glGetTexLevelParameterf(int target, int level, int pname) { return GL11C.glGetTexLevelParameterf(target, level, pname); }
    public static void glGetTexParameteriv(int target, int pname, IntBuffer params) { GL11C.glGetTexParameteriv(target, pname, params); }
    public static int glGetTexParameteri(int target, int pname) { return GL11C.glGetTexParameteri(target, pname); }
    public static void glGetTexParameterfv(int target, int pname, FloatBuffer params) { GL11C.glGetTexParameterfv(target, pname, params); }
    public static float glGetTexParameterf(int target, int pname) { return GL11C.glGetTexParameterf(target, pname); }
    public static void glLineWidth(float width) { GL11C.glLineWidth(width); }
    public static void glPointSize(float size) { GL11C.glPointSize(size); }
    public static void glPixelStorei(int pname, int param) { GL11C.glPixelStorei(pname, param); }
    public static void glPixelStoref(int pname, float param) { GL11C.glPixelStoref(pname, param); }
    public static void glPolygonMode(int face, int mode) { GL11C.glPolygonMode(face, mode); }
    public static void glPolygonOffset(float factor, float units) { GL11C.glPolygonOffset(factor, units); }
    public static void glReadBuffer(int mode) { GL11C.glReadBuffer(mode); }
    public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) { GL11C.glReadPixels(x, y, width, height, format, type, pixels); }
    public static void glReadPixels(int x, int y, int width, int height, int format, int type, ShortBuffer pixels) { GL11C.glReadPixels(x, y, width, height, format, type, pixels); }
    public static void glReadPixels(int x, int y, int width, int height, int format, int type, IntBuffer pixels) { GL11C.glReadPixels(x, y, width, height, format, type, pixels); }
    public static void glReadPixels(int x, int y, int width, int height, int format, int type, FloatBuffer pixels) { GL11C.glReadPixels(x, y, width, height, format, type, pixels); }
    public static void glReadPixels(int x, int y, int width, int height, int format, int type, long pixels) { GL11C.glReadPixels(x, y, width, height, format, type, pixels); }
    public static void glScissor(int x, int y, int width, int height) { GL11C.glScissor(x, y, width, height); }
    public static void glStencilFunc(int func, int ref, int mask) { GL11C.glStencilFunc(func, ref, mask); }
    public static void glStencilMask(int mask) { GL11C.glStencilMask(mask); }
    public static void glStencilOp(int fail, int zfail, int zpass) { GL11C.glStencilOp(fail, zfail, zpass); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage1D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels) { GL11C.glTexImage1D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels) { GL11C.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels); }
    public static void glCopyTexImage1D(int target, int level, int internalformat, int x, int y, int width, int border) { GL11C.glCopyTexImage1D(target, level, internalformat, x, y, width, border); }
    public static void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) { GL11C.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border); }
    public static void glCopyTexSubImage1D(int target, int level, int xoffset, int x, int y, int width) { GL11C.glCopyTexSubImage1D(target, level, xoffset, x, y, width); }
    public static void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) { GL11C.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height); }
    public static void glTexParameteri(int target, int pname, int param) { GL11C.glTexParameteri(target, pname, param); }
    public static void glTexParameteriv(int target, int pname, IntBuffer param) { GL11C.glTexParameteriv(target, pname, param); }
    public static void glTexParameterf(int target, int pname, float param) { GL11C.glTexParameterf(target, pname, param); }
    public static void glTexParameterfv(int target, int pname, FloatBuffer param) { GL11C.glTexParameterfv(target, pname, param); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ByteBuffer pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ShortBuffer pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, IntBuffer pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, FloatBuffer pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, DoubleBuffer pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, long pixels) { GL11C.glTexSubImage1D(target, level, xoffset, width, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ShortBuffer pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, FloatBuffer pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, DoubleBuffer pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels) { GL11C.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels); }
    public static void glViewport(int x, int y, int width, int height) { GL11C.glViewport(x, y, width, height); }
    public static void glBlendFunc(int sfactor, int dfactor) { GL11C.glBlendFunc(sfactor, dfactor); }
    public static void glMatrixMode(int mode) { GL11C.glMatrixMode(mode); }
    public static void glLoadIdentity() { GL11C.glLoadIdentity(); }
    public static void glLoadMatrixf(FloatBuffer m) { GL11C.glLoadMatrixf(m); }
    public static void glMultMatrixf(FloatBuffer m) { GL11C.glMultMatrixf(m); }
    public static void glPushMatrix() { GL11C.glPushMatrix(); }
    public static void glPopMatrix() { GL11C.glPopMatrix(); }
    public static void glTranslatef(float x, float y, float z) { GL11C.glTranslatef(x, y, z); }
    public static void glRotatef(float angle, float x, float y, float z) { GL11C.glRotatef(angle, x, y, z); }
    public static void glScalef(float x, float y, float z) { GL11C.glScalef(x, y, z); }
    public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) { GL11C.glOrtho(left, right, bottom, top, zNear, zFar); }
    public static void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) { GL11C.glFrustum(left, right, bottom, top, zNear, zFar); }
    public static void glLookAt(double eyex, double eyey, double eyez, double centerx, double centery, double centerz, double upx, double upy, double upz) { GL11C.glLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz); }
    public static void glBegin(int mode) { GL11C.glBegin(mode); }
    public static void glEnd() { GL11C.glEnd(); }
    public static void glVertex2f(float x, float y) { GL11C.glVertex2f(x, y); }
    public static void glVertex2i(int x, int y) { GL11C.glVertex2i(x, y); }
    public static void glVertex2s(short x, short y) { GL11C.glVertex2s(x, y); }
    public static void glVertex3f(float x, float y, float z) { GL11C.glVertex3f(x, y, z); }
    public static void glVertex3i(int x, int y, int z) { GL11C.glVertex3i(x, y, z); }
    public static void glVertex3s(short x, short y, short z) { GL11C.glVertex3s(x, y, z); }
    public static void glVertex4f(float x, float y, float z, float w) { GL11C.glVertex4f(x, y, z, w); }
    public static void glVertex4i(int x, int y, int z, int w) { GL11C.glVertex4i(x, y, z, w); }
    public static void glVertex4s(short x, short y, short z, short w) { GL11C.glVertex4s(x, y, z, w); }
    public static void glColor3f(float red, float green, float blue) { GL11C.glColor3f(red, green, blue); }
    public static void glColor4f(float red, float green, float blue, float alpha) { GL11C.glColor4f(red, green, blue, alpha); }
    public static void glColor3ub(byte red, byte green, byte blue) { GL11C.glColor3ub(red, green, blue); }
    public static void glColor4ub(byte red, byte green, byte blue, byte alpha) { GL11C.glColor4ub(red, green, blue, alpha); }
    public static void glNormal3f(float nx, float ny, float nz) { GL11C.glNormal3f(nx, ny, nz); }
    public static void glTexCoord2f(float s, float t) { GL11C.glTexCoord2f(s, t); }
    public static void glTexCoord2i(int s, int t) { GL11C.glTexCoord2i(s, t); }
    public static void glTexCoord2s(short s, short t) { GL11C.glTexCoord2s(s, t); }
    public static void glTexCoord3f(float s, float t, float r) { GL11C.glTexCoord3f(s, t, r); }
    public static void glTexCoord4f(float s, float t, float r, float q) { GL11C.glTexCoord4f(s, t, r, q); }
    public static void glIndexd(double index) { GL11C.glIndexd(index); }
    public static void glIndexf(float index) { GL11C.glIndexf(index); }
    public static void glIndexi(int index) { GL11C.glIndexi(index); }
    public static void glIndexs(short index) { GL11C.glIndexs(index); }
    public static void glShadeModel(int mode) { GL11C.glShadeModel(mode); }
    public static void glFogf(int pname, float param) { GL11C.glFogf(pname, param); }
    public static void glFogfv(int pname, FloatBuffer params) { GL11C.glFogfv(pname, params); }
    public static void glFogi(int pname, int param) { GL11C.glFogi(pname, param); }
    public static void glFogiv(int pname, IntBuffer params) { GL11C.glFogiv(pname, params); }
    public static void glLightf(int light, int pname, float param) { GL11C.glLightf(light, pname, param); }
    public static void glLightfv(int light, int pname, FloatBuffer params) { GL11C.glLightfv(light, pname, params); }
    public static void glLighti(int light, int pname, int param) { GL11C.glLighti(light, pname, param); }
    public static void glLightiv(int light, int pname, IntBuffer params) { GL11C.glLightiv(light, pname, params); }
    public static void glLightModelf(int pname, float param) { GL11C.glLightModelf(pname, param); }
    public static void glLightModelfv(int pname, FloatBuffer params) { GL11C.glLightModelfv(pname, params); }
    public static void glLightModeli(int pname, int param) { GL11C.glLightModeli(pname, param); }
    public static void glLightModeliv(int pname, IntBuffer params) { GL11C.glLightModeliv(pname, params); }
    public static void glMaterialf(int face, int pname, float param) { GL11C.glMaterialf(face, pname, param); }
    public static void glMaterialfv(int face, int pname, FloatBuffer params) { GL11C.glMaterialfv(face, pname, params); }
    public static void glMateriali(int face, int pname, int param) { GL11C.glMateriali(face, pname, param); }
    public static void glMaterialiv(int face, int pname, IntBuffer params) { GL11C.glMaterialiv(face, pname, params); }
    public static void glColorMaterial(int face, int color) { GL11C.glColorMaterial(face, color); }
    public static void glPolygonStipple(ByteBuffer mask) { GL11C.glPolygonStipple(mask); }
    public static void glClipPlane(int plane, FloatBuffer equation) { GL11C.glClipPlane(plane, equation); }
    public static void glCopyPixels(int x, int y, int width, int height, int type) { GL11C.glCopyPixels(x, y, width, height, type); }
    public static void glRasterPos2f(float x, float y) { GL11C.glRasterPos2f(x, y); }
    public static void glRasterPos3f(float x, float y, float z) { GL11C.glRasterPos3f(x, y, z); }
    public static void glRasterPos4f(float x, float y, float z, float w) { GL11C.glRasterPos4f(x, y, z, w); }
    public static void glCallList(int list) { GL11C.glCallList(list); }
    public static void glCallLists(IntBuffer lists) { GL11C.glCallLists(lists); }
    public static void glNewList(int list, int mode) { GL11C.glNewList(list, mode); }
    public static void glEndList() { GL11C.glEndList(); }
    public static void glDeleteLists(int list, int range) { GL11C.glDeleteLists(list, range); }
    public static int glGenLists(int range) { return GL11C.glGenLists(range); }
    public static void glAccum(int op, float value) { GL11C.glAccum(op, value); }
    public static void glClearAccum(float red, float green, float blue, float alpha) { GL11C.glClearAccum(red, green, blue, alpha); }
    public static void glFeedbackBuffer(IntBuffer buffer, int type) { GL11C.glFeedbackBuffer(buffer, type); }
    public static void glSelectBuffer(IntBuffer buffer) { GL11C.glSelectBuffer(buffer); }
    public static void glRenderMode(int mode) { GL11C.glRenderMode(mode); }
    public static void glVertexPointer(int size, int type, int stride, FloatBuffer buf) { GL11C.glVertexPointer(size, type, stride, buf); }
    public static void glVertexPointer(int size, int type, int stride, int offset) { GL11C.glVertexPointer(size, type, stride, offset); }
    public static void glColorPointer(int size, int type, int stride, FloatBuffer ptr) { GL11C.glColorPointer(size, type, stride, ptr); }
    public static void glColorPointer(int size, int type, int stride, int offset) { GL11C.glColorPointer(size, type, stride, offset); }
    public static void glTexCoordPointer(int size, int type, int stride, FloatBuffer ptr) { GL11C.glTexCoordPointer(size, type, stride, ptr); }
    public static void glTexCoordPointer(int size, int type, int stride, int offset) { GL11C.glTexCoordPointer(size, type, stride, offset); }
    public static void glNormalPointer(int type, int stride, FloatBuffer ptr) { GL11C.glNormalPointer(type, stride, ptr); }
    public static void glNormalPointer(int type, int stride, int offset) { GL11C.glNormalPointer(type, stride, offset); }
    public static void glEnableClientState(int cap) { GL11C.glEnableClientState(cap); }
    public static void glDisableClientState(int cap) { GL11C.glDisableClientState(cap); }

    // --- GL12 ---
    public static void glDrawRangeElements(int mode, int start, int end, int count, int type, IntBuffer indices) { GL12C.glDrawRangeElements(mode, start, end, count, type, indices); }
    public static void glDrawRangeElements(int mode, int start, int end, int count, int type, int indices) { GL12C.glDrawRangeElements(mode, start, end, count, type, indices); }
    public static void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) { GL12C.glDrawRangeElements(mode, start, end, count, type, indices); }
    public static void glDrawRangeElements(int mode, int start, int end, int count, int type, long indices) { GL12C.glDrawRangeElements(mode, start, end, count, type, indices); }
    public static void glGenerateMipmap(int target) { GL12C.glGenerateMipmap(target); }
    public static void glWindowPos2f(float x, float y) { GL12C.glWindowPos2f(x, y); }
    public static void glWindowPos3f(float x, float y, float z) { GL12C.glWindowPos3f(x, y, z); }
    public static void glFogCoordf(float coord) { GL12C.glFogCoordf(coord); }
    public static void glFogCoordfv(FloatBuffer coords) { GL12C.glFogCoordfv(coords); }
    public static void glSecondaryColor3f(float red, float green, float blue) { GL12C.glSecondaryColor3f(red, green, blue); }
    public static void glSecondaryColor3fv(FloatBuffer color) { GL12C.glSecondaryColor3fv(color); }
    public static void glPointParameterf(int pname, float param) { GL12C.glPointParameterf(pname, param); }
    public static void glPointParameterfv(int pname, FloatBuffer params) { GL12C.glPointParameterfv(pname, params); }
    public static void glPointParameteri(int pname, int param) { GL12C.glPointParameteri(pname, param); }
    public static void glPointParameteriv(int pname, IntBuffer params) { GL12C.glPointParameteriv(pname, params); }

    // --- GL13 ---
    public static void glMultiTexCoord2f(int target, float s, float t) { GL13C.glMultiTexCoord2f(target, s, t); }
    public static void glMultiTexCoord3f(int target, float s, float t, float r) { GL13C.glMultiTexCoord3f(target, s, t, r); }
    public static void glMultiTexCoord4f(int target, float s, float t, float r, float q) { GL13C.glMultiTexCoord4f(target, s, t, r, q); }
    public static void glActiveTexture(int texture) { GL13C.glActiveTexture(texture); }
    public static void glClientActiveTexture(int texture) { GL13C.glClientActiveTexture(texture); }
    public static void glSampleCoverage(float value, boolean invert) { GL13C.glSampleCoverage(value, invert); }
    public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, ByteBuffer data) { GL13C.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data); }
    public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) { GL13C.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data); }
    public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, long data) { GL13C.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data); }
    public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, ByteBuffer data) { GL13C.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data); }
    public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) { GL13C.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data); }
    public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, long data) { GL13C.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data); }
    public static void glGetCompressedTexImage(int target, int level, ByteBuffer img) { GL13C.glGetCompressedTexImage(target, level, img); }
    public static void glGetCompressedTexImage(int target, int level, Buffer img) { GL13C.glGetCompressedTexImage(target, level, img); }
    public static void glGetCompressedTexImage(int target, int level, long img) { GL13C.glGetCompressedTexImage(target, level, img); }

    // --- GL14 ---
    public static void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) { GL14C.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha); }
    public static void glBlendEquation(int mode) { GL14C.glBlendEquation(mode); }
    public static void glBlendColor(float red, float green, float blue, float alpha) { GL14C.glBlendColor(red, green, blue, alpha); }

    // --- GL15 ---
    public static void glBindBuffer(int target, int buffer) { GL15C.glBindBuffer(target, buffer); }
    public static void glDeleteBuffers(IntBuffer buffers) { GL15C.glDeleteBuffers(buffers); }
    public static void glDeleteBuffers(int buffer) { GL15C.glDeleteBuffers(buffer); }
    public static void glGenBuffers(IntBuffer buffers) { GL15C.glGenBuffers(buffers); }
    public static int glGenBuffers() { return GL15C.glGenBuffers(); }
    public static boolean glIsBuffer(int buffer) { return GL15C.glIsBuffer(buffer); }
    public static void glBufferData(int target, long size, int usage) { GL15C.glBufferData(target, size, usage); }
    public static void glBufferData(int target, ByteBuffer data, int usage) { GL15C.glBufferData(target, data, usage); }
    public static void glBufferData(int target, IntBuffer data, int usage) { GL15C.glBufferData(target, data, usage); }
    public static void glBufferData(int target, FloatBuffer data, int usage) { GL15C.glBufferData(target, data, usage); }
    public static void glBufferSubData(int target, long offset, ByteBuffer data) { GL15C.glBufferSubData(target, offset, data); }
    public static void glBufferSubData(int target, long offset, IntBuffer data) { GL15C.glBufferSubData(target, offset, data); }
    public static void glBufferSubData(int target, long offset, FloatBuffer data) { GL15C.glBufferSubData(target, offset, data); }
    public static void glGetBufferParameteriv(int target, int pname, IntBuffer data) { GL15C.glGetBufferParameteriv(target, pname, data); }
    public static int glGetBufferParameteri(int target, int pname) { return GL15C.glGetBufferParameteri(target, pname); }
    public static void glGenQueries(IntBuffer ids) { GL15C.glGenQueries(ids); }
    public static int glGenQueries() { return GL15C.glGenQueries(); }
    public static void glDeleteQueries(IntBuffer ids) { GL15C.glDeleteQueries(ids); }
    public static void glDeleteQueries(int id) { GL15C.glDeleteQueries(id); }
    public static boolean glIsQuery(int id) { return GL15C.glIsQuery(id); }
    public static void glBeginQuery(int target, int id) { GL15C.glBeginQuery(target, id); }
    public static void glEndQuery(int target) { GL15C.glEndQuery(target); }
    public static void glGetQueryiv(int target, int pname, IntBuffer data) { GL15C.glGetQueryiv(target, pname, data); }
    public static int glGetQueryi(int target, int pname) { return GL15C.glGetQueryi(target, pname); }
    public static void glGetQueryObjectiv(int id, int pname, IntBuffer data) { GL15C.glGetQueryObjectiv(id, pname, data); }
    public static int glGetQueryObjecti(int id, int pname) { return GL15C.glGetQueryObjecti(id, pname); }
}   
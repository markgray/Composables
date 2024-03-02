/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetlagged

import android.graphics.Color
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import com.example.jetlagged.ui.theme.White
import com.example.jetlagged.ui.theme.Yellow
import com.example.jetlagged.ui.theme.YellowVariant
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

/**
 * A [Modifier.Element] which manages an instance of a particular [Modifier.Node] implementation.
 * A given [Modifier.Node] implementation can only be used when a [ModifierNodeElement] which
 * creates and updates that implementation is applied to a Layout. A [ModifierNodeElement] should
 * be very lightweight, and do little more than hold the information necessary to create and
 * maintain an instance of the associated [Modifier.Node] type.
 *
 * The [Modifier.Node] type that we create and maintain is [YellowBackgroundNode].
 */
private data object YellowBackgroundElement : ModifierNodeElement<YellowBackgroundNode>() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    /**
     * This will be called the first time the modifier is applied to the Layout and it should
     * construct and return the corresponding [Modifier.Node] instance.
     */
    override fun create() = YellowBackgroundNode()

    /**
     * Populates an [InspectorInfo] object with attributes to display in the layout inspector. This
     * is called by tooling to resolve the properties of this modifier. By convention, implementors
     * should set the name to the function name of the modifier. We set [InspectorInfo.name] to the
     * name of the [Modifier] "yellowBackground".
     */
    override fun InspectorInfo.inspectableProperties() {
        name = "yellowBackground"
    }

    /**
     * Called when a modifier is applied to a Layout whose inputs have changed from the previous
     * application. This function will have the current node instance passed in as a parameter, and
     * it is expected that the node will be brought up to date.
     *
     * @param node the current [Modifier.Node] instance.
     */
    override fun update(node: YellowBackgroundNode) {
    }
}

/**
 * This custom [DrawModifierNode] is created and maintained by [YellowBackgroundElement] and used
 * when [Build.VERSION.SDK_INT] >= [Build.VERSION_CODES.TIRAMISU] as the implementation for our
 * custom [Modifier.yellowBackground]
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class YellowBackgroundNode : DrawModifierNode, Modifier.Node() {

    /**
     * This is the [RuntimeShader] created from the text of the AGSL shader program [SHADER]. It runs
     * the AGSL shader program to create the current [ShaderBrush] field [shaderBrush].
     */
    private val shader: RuntimeShader = RuntimeShader(SHADER)

    /**
     * This [ShaderBrush] uses the animated [RuntimeShader] field [shader] to create an animated
     * gradient brush that is used as `brush` argument when calling the [ContentDrawScope.drawRect]
     * method.
     */
    private val shaderBrush: ShaderBrush = ShaderBrush(shader = shader)

    /**
     * The [MutableFloatState] that is used to animate the "time" uniform value of the [SHADER] AGSL
     * program that the [RuntimeShader] field [shader] is running.
     */
    private val time: MutableFloatState = mutableFloatStateOf(value = 0f)

    init {
        shader.setColorUniform(
            "color",
            Color.valueOf(Yellow.red, Yellow.green, Yellow.blue, Yellow.alpha)
        )
    }

    /**
     * This is an override of the [DrawModifierNode] `ContentDrawScope.draw` method. The call to
     * [ContentDrawScope.drawContent] causes child drawing operations to run during the onPaint
     * lambda. This is a [Modifier.Node] that draws into the space of the layout, and is the
     * [androidx.compose.ui.Modifier.Node] equivalent of [androidx.compose.ui.draw.DrawModifier].
     * First we set the uniform value "resolution" corresponding to our [RuntimeShader] field [shader]
     * to the current value of the [Size.width] and [Size.height] of [ContentDrawScope.size], then we
     * set its uniform value "time" to the current value of our animated [MutableFloatState] field
     * [time]. We call the [ContentDrawScope.drawRect] method with its `brush` argument our [ShaderBrush]
     * field [shaderBrush] to draw a rectangle from the top left the size of the current environment.
     * Finally we call [ContentDrawScope.drawContent] to have child drawing operations run during the
     * onPaint.
     */
    override fun ContentDrawScope.draw() {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time.floatValue)
        drawRect(brush = shaderBrush)
        drawContent()
    }

    /**
     * Called when the node is attached to a [androidx.compose.ui.layout.Layout] which is part of
     * the UI tree. When called, node is guaranteed to be non-`null`. You can call `sideEffect`,
     * `coroutineScope`, etc. This is not guaranteed to get called at a time where the rest of the
     * Modifier.Nodes in the hierarchy are "up to date". For instance, at the time of calling
     * `onAttach` for this node, another node may be in the tree that will be detached by the time
     * Compose has finished applying changes. As a result, if you need to guarantee that the state
     * of the tree is "final" for this round of changes, you should use the `sideEffect` API to
     * schedule the calculation to be done at that time.
     */
    override fun onAttach() {
        coroutineScope.launch {
            while (true) {
                withInfiniteAnimationFrameMillis {
                    time.floatValue = it / 1000f
                }
            }
        }
    }
}

/**
 * This creates the [Modifier.yellowBackground] that is used to draw a nifty animated "Ocean Wave"
 * effect for the [Column] holding the [JetLaggedHeader] and the [JetLaggedSleepSummary] that is at
 * the top of [JetLaggedScreen]. If [Build.VERSION.SDK_INT] >= [Build.VERSION_CODES.TIRAMISU] it
 * chains [YellowBackgroundElement] to its [Modifier] receiver, otherwise it uses [drawWithCache]
 * to cache the [Brush.verticalGradient] vertical gradient it creates then uses as the `brush`
 * argument when it calls [DrawScope.drawRect] within the [CacheDrawScope.onDrawBehind] method to
 * issue drawing commands to be executed before the layout content is drawn.
 */
fun Modifier.yellowBackground(): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.then(YellowBackgroundElement)
    } else {
        drawWithCache {

            val gradientBrush: Brush = Brush.verticalGradient(colors = listOf(Yellow, YellowVariant, White))
            onDrawBehind {
                drawRect(brush = gradientBrush)
            }
        }
    }

/**
 * This is the "AGSL" program used by the [RuntimeShader] variable `shader` in [YellowBackgroundNode].
 * See [Android Graphics Shading Language](https://developer.android.com/develop/ui/views/graphics/agsl)
 */
@Language("AGSL")
val SHADER: String = """
    uniform float2 resolution;
    uniform float time;
    layout(color) uniform half4 color;
    
    float calculateColorMultiplier(float yCoord, float factor) {
        return step(yCoord, 1.0 + factor * 2.0) - step(yCoord, factor - 0.1);
    }

    float4 main(in float2 fragCoord) {
        // Config values
        const float speedMultiplier = 1.5;
        const float waveDensity = 1.0;
        const float loops = 8.0;
        const float energy = 0.6;
        
        // Calculated values
        float2 uv = fragCoord / resolution.xy;
        float3 rgbColor = color.rgb;
        float timeOffset = time * speedMultiplier;
        float hAdjustment = uv.x * 4.3;
        float3 loopColor = vec3(1.0 - rgbColor.r, 1.0 - rgbColor.g, 1.0 - rgbColor.b) / loops;
        
        for (float i = 1.0; i <= loops; i += 1.0) {
            float loopFactor = i * 0.1;
            float sinInput = (timeOffset + hAdjustment) * energy;
            float curve = sin(sinInput) * (1.0 - loopFactor) * 0.05;
            float colorMultiplier = calculateColorMultiplier(uv.y, loopFactor);
            rgbColor += loopColor * colorMultiplier;
            
            // Offset for next loop
            uv.y += curve;
        }
        
        return float4(rgbColor, 1.0);
    }
""".trimIndent()

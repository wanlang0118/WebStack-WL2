<template>
  <div class="chart-container">
    <v-chart class="chart" :option="option" autoresize />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useThemeStore } from '../../store/theme'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent])

const themeStore = useThemeStore()

const props = defineProps({
  data: {
    type: Array,
    required: true
  }
})

const option = computed(() => {
  const isDark = themeStore.isDark
  const tooltipBg = isDark ? '#1F1F26' : '#FFFFFF'
  const tooltipBorder = isDark ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.1)'
  const tooltipText = isDark ? '#FFFFFF' : '#000000'

  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipText, fontSize: 13 },
      padding: [8, 12]
    },
    color: ['#F97316', '#FB923C', '#FDBA74', '#FED7AA', '#FFEDD5', '#3B82F6'],
    series: [
      {
        name: '分类占比',
        type: 'pie',
        radius: ['55%', '85%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: isDark ? '#17171C' : '#FFFFFF',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold',
            color: isDark ? '#FFFFFF' : '#000000',
            formatter: '{b}\n{c} 个'
          }
        },
        labelLine: {
          show: false
        },
        data: props.data
      }
    ]
  }
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  min-height: 280px;
}
.chart {
  width: 100%;
  height: 100%;
}
</style>

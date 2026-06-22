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
import { LineChart } from 'echarts/charts'
import { 
  GridComponent, 
  TooltipComponent, 
  LegendComponent 
} from 'echarts/components'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const themeStore = useThemeStore()

const props = defineProps({
  data: {
    type: Object,
    required: true
  }
})

const option = computed(() => {
  const isDark = themeStore.isDark
  const textColor = isDark ? 'rgba(255, 255, 255, 0.5)' : 'rgba(0, 0, 0, 0.45)'
  const splitLineColor = isDark ? 'rgba(255, 255, 255, 0.04)' : 'rgba(0, 0, 0, 0.04)'
  const tooltipBg = isDark ? '#1F1F26' : '#FFFFFF'
  const tooltipBorder = isDark ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.1)'
  const tooltipText = isDark ? '#FFFFFF' : '#000000'

  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: tooltipBg,
      borderColor: tooltipBorder,
      textStyle: { color: tooltipText, fontSize: 13 },
      padding: [8, 12],
      axisPointer: {
        type: 'line',
        lineStyle: {
          color: isDark ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'
        }
      }
    },
    legend: {
      data: ['访问点击量', '活跃点赞量'],
      top: 0,
      right: 0,
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: {
        color: textColor,
        fontSize: 12
      }
    },
    grid: {
      left: '0%',
      right: '0%',
      bottom: '0%',
      top: '40px',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.data.dates,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: textColor,
        margin: 16
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: splitLineColor,
          type: 'dashed'
        }
      },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: textColor,
        margin: 16
      }
    },
    series: [
      {
        name: '访问点击量',
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: {
          width: 2,
          color: '#F97316'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(249, 115, 22, 0.2)' },
              { offset: 1, color: 'rgba(249, 115, 22, 0)' }
            ]
          }
        },
        data: props.data.clicks
      },
      {
        name: '活跃点赞量',
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: {
          width: 2,
          color: '#3B82F6'
        },
        data: props.data.likes
      }
    ]
  }
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  min-height: 320px;
}
.chart {
  width: 100%;
  height: 100%;
}
</style>

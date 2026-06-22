const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, ImageRun,
        Header, Footer, AlignmentType, LevelFormat, HeadingLevel,
        TableOfContents, PageBreak, PageNumber, BorderStyle, WidthType, ShadingType } = require('docx');
const fs = require('fs');

// 定义文档内容
const doc = new Document({
  styles: {
    default: {
      document: {
        run: { font: "宋体", size: 24 } // 12pt
      }
    },
    paragraphStyles: [
      {
        id: "Heading1",
        name: "Heading 1",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 32, bold: true, font: "黑体" },
        paragraph: { spacing: { before: 240, after: 240 }, outlineLevel: 0 }
      },
      {
        id: "Heading2",
        name: "Heading 2",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 28, bold: true, font: "黑体" },
        paragraph: { spacing: { before: 180, after: 180 }, outlineLevel: 1 }
      },
      {
        id: "Heading3",
        name: "Heading 3",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 24, bold: true, font: "黑体" },
        paragraph: { spacing: { before: 120, after: 120 }, outlineLevel: 2 }
      }
    ]
  },
  numbering: {
    config: [
      {
        reference: "bullets",
        levels: [{
          level: 0,
          format: LevelFormat.BULLET,
          text: "\u2022",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } }
        }]
      },
      {
        reference: "numbers",
        levels: [{
          level: 0,
          format: LevelFormat.DECIMAL,
          text: "%1.",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } }
        }]
      }
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 11906, height: 16838 }, // A4
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    headers: {
      default: new Header({
        children: [new Paragraph({ children: [new TextRun("专业实训V（JavaEE项目）设计报告书")] })]
      })
    },
    footers: {
      default: new Footer({
        children: [new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [new TextRun("第 "), new TextRun({ children: [PageNumber.CURRENT] }), new TextRun(" 页")]
        })]
      })
    },
    children: [
      // 封面
      new Paragraph({ spacing: { before: 2400 } }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "专业实训V（JavaEE项目）设计报告书", bold: true, size: 44, font: "黑体" })]
      }),
      new Paragraph({ spacing: { before: 600 } }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "WebStack-WL2 网站导航系统", bold: true, size: 36, font: "黑体" })]
      }),
      new Paragraph({ spacing: { before: 1200 } }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "学    院：信息工程学院", size: 28, font: "宋体" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "专    业：软件工程", size: 28, font: "宋体" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "班    级：软件工程2201班", size: 28, font: "宋体" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "学    号：1650730033", size: 28, font: "宋体" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "姓    名：王鹏程", size: 28, font: "宋体" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "指导教师：张老师", size: 28, font: "宋体" })]
      }),
      new Paragraph({ spacing: { before: 600 } }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "2026年6月", size: 28, font: "宋体" })]
      }),
      
      // 目录页
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("目录")]
      }),
      new TableOfContents("目录", { hyperlink: true, headingStyleRange: "1-3" }),
      
      // 第一章 引言
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第一章 引言")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("1.1 项目背景")]
      }),
      new Paragraph({
        children: [new TextRun("随着互联网技术的快速发展，各种在线工具和网站层出不穷，用户面临着信息过载的问题。为了帮助用户快速找到所需的工具和资源，网站导航系统应运而生。本项目旨在开发一个功能完善的网站导航系统，为用户提供便捷的工具发现和管理服务。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("1.2 项目目的")]
      }),
      new Paragraph({
        children: [new TextRun("本项目的主要目的是设计并实现一个基于Spring Boot和Vue3的前后端分离网站导航系统，实现网站的分类管理、搜索、评论、点赞等功能，同时提供管理后台供管理员维护网站数据。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("1.3 项目意义")]
      }),
      new Paragraph({
        children: [new TextRun("本项目的意义在于：")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("为用户提供一个集中化的网站导航平台，方便用户发现和使用各类在线工具")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("实现前后端分离架构，提高系统的可维护性和可扩展性")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("通过用户交互功能（评论、点赞）增强用户粘性和社区活跃度")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("利用AI技术实现批量导入，提高数据管理效率")]
      }),
      
      // 第二章 技术介绍
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第二章 技术介绍")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("2.1 Spring Boot")]
      }),
      new Paragraph({
        children: [new TextRun("Spring Boot是一个基于Spring框架的快速开发脚手架，它简化了Spring应用的初始搭建和开发过程。本项目使用Spring Boot 3.3.5版本，提供了RESTful API接口，实现了后端业务逻辑。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("2.2 Vue3")]
      }),
      new Paragraph({
        children: [new TextRun("Vue3是一个渐进式JavaScript框架，用于构建用户界面。本项目使用Vue3的Composition API，结合Vite构建工具，实现了响应式前端页面。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("2.3 MyBatis-Plus")]
      }),
      new Paragraph({
        children: [new TextRun("MyBatis-Plus是一个MyBatis的增强工具，在MyBatis的基础上只做增强不做改变，为简化开发、提高效率而生。本项目使用MyBatis-Plus 3.5.10.1版本，简化了数据库操作。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("2.4 其他技术")]
      }),
      new Paragraph({
        children: [new TextRun("本项目还使用了以下技术：")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("MySQL：关系型数据库，存储系统数据")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("JWT：JSON Web Token，用于用户认证和授权")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("naive-ui：Vue3的UI组件库，用于管理后台界面开发")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("DeepSeek API：AI接口，用于批量导入功能的文本解析")]
      }),
      
      // 第三章 可行性分析与需求分析
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第三章 可行性分析与需求分析")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("3.1 可行性分析")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("3.1.1 技术可行性")]
      }),
      new Paragraph({
        children: [new TextRun("本项目采用的Spring Boot、Vue3、MyBatis-Plus等技术都是目前主流的开发技术，具有成熟的技术生态和丰富的文档资源。开发团队对这些技术有较好的掌握，能够保证项目的顺利开发。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("3.1.2 经济可行性")]
      }),
      new Paragraph({
        children: [new TextRun("本项目开发成本较低，主要使用开源技术栈，无需支付昂贵的软件许可费用。服务器采用本地部署方式，硬件成本可控。项目开发周期短，人力成本在可接受范围内。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("3.1.3 操作可行性")]
      }),
      new Paragraph({
        children: [new TextRun("本系统采用B/S架构，用户只需通过浏览器即可访问系统，无需安装额外软件。管理后台界面设计直观，操作简便，管理员可以快速上手使用。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("3.2 需求分析")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("3.2.1 功能需求")]
      }),
      new Paragraph({
        children: [new TextRun("本系统主要包含以下功能模块：")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("用户管理：包括用户注册、登录、个人信息管理等功能")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("网站管理：包括网站的增删改查、分类管理、标签管理等功能")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("内容管理：包括网站详情小节、内容编辑等功能")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("交互功能：包括评论、点赞、点击记录等功能")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("批量导入：支持通过AI解析批量导入网站数据")]
      }),
      new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun("消息通知：支持系统公告和用户间消息通知")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("3.2.2 非功能需求")]
      }),
      new Paragraph({
        children: [new TextRun("系统需要满足以下非功能需求：")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("性能：系统应能支持并发访问，响应时间在可接受范围内")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("安全性：系统应实现用户认证和授权，防止未授权访问")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("可用性：系统界面应简洁友好，操作流程直观")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("可维护性：系统应采用模块化设计，代码结构清晰")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("可扩展性：系统应支持后续功能扩展和性能优化")]
      }),
      
      // 第四章 系统概要设计
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第四章 系统概要设计")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("4.1 系统架构设计")]
      }),
      new Paragraph({
        children: [new TextRun("本系统采用前后端分离架构，后端提供RESTful API接口，前端通过HTTP请求调用后端服务。系统整体架构分为三层：表现层、业务逻辑层和数据访问层。")]
      }),
      new Paragraph({
        children: [new TextRun("表现层：包括用户前端和管理后台，使用Vue3框架开发，负责用户界面展示和交互。")]
      }),
      new Paragraph({
        children: [new TextRun("业务逻辑层：使用Spring Boot框架开发，负责处理业务逻辑，提供API接口。")]
      }),
      new Paragraph({
        children: [new TextRun("数据访问层：使用MyBatis-Plus框架，负责与MySQL数据库交互，实现数据持久化。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("4.2 功能模块设计")]
      }),
      new Paragraph({
        children: [new TextRun("系统主要功能模块如下：")]
      }),
      
      // 功能模块表格
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [2000, 3000, 4026],
        rows: [
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "模块名称", bold: true })] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "功能描述", bold: true })] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "主要接口", bold: true })] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("认证模块")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("用户登录认证")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("POST /api/auth/login")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("分类模块")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站分类管理")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("GET /api/category/tree")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站模块")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站信息管理")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("GET /api/site/page")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("评论模块")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站评论管理")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("GET /api/site-comment/page")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("点赞模块")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站点赞功能")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("POST /api/site-like/toggle")] })]
              })
            ]
          })
        ]
      }),
      
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("4.3 数据库设计")]
      }),
      new Paragraph({
        children: [new TextRun("系统数据库采用MySQL，共设计10张核心表，主要包括：")]
      }),
      
      // 数据库表设计表格
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [2000, 3000, 4026],
        rows: [
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "表名", bold: true })] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "用途", bold: true })] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                shading: { fill: "D5E8F0", type: ShadingType.CLEAR },
                children: [new Paragraph({ children: [new TextRun({ text: "主要字段", bold: true })] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("category")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("分类表")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("id, parent_id, title, icon, sort")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("site")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站主表")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("id, category_id, title, url, description")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("sys_user")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("系统用户表")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("id, username, password, role, status")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("site_comment")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("网站评论表")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("id, site_id, user_id, content, status")] })]
              })
            ]
          }),
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("site_like")] })]
              }),
              new TableCell({
                width: { size: 3000, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("点赞记录表")] })]
              }),
              new TableCell({
                width: { size: 4026, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("id, site_id, user_id, ip")] })]
              })
            ]
          })
        ]
      }),
      
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("4.4 用户实体设计")]
      }),
      new Paragraph({
        children: [new TextRun("系统用户实体包含以下属性：")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("用户ID：唯一标识用户")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("用户名：用于登录")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("密码：加密存储")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("角色：ADMIN（管理员）或USER（普通用户）")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("状态：启用或禁用")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("最后登录时间：记录用户最后登录时间")]
      }),
      
      // 第五章 系统详细设计
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第五章 系统详细设计与实现")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("5.1 用户前端实现")]
      }),
      new Paragraph({
        children: [new TextRun("用户前端采用Vue3框架开发，主要实现以下功能：")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("5.1.1 首页展示")]
      }),
      new Paragraph({
        children: [new TextRun("首页展示所有网站分类和网站列表，用户可以通过侧边栏快速定位分类，通过搜索框查找特定网站。")]
      }),
      new Paragraph({
        children: [new TextRun("图5-1 用户前端首页界面")]
      }),
      new Paragraph({
        children: [new ImageRun({
          type: "png",
          data: fs.readFileSync("../2026-06-11_10-34-46.png"),
          transformation: { width: 400, height: 300 },
          altText: { title: "用户前端首页", description: "用户前端首页界面截图", name: "首页截图" }
        })]
      }),
      
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("5.1.2 网站详情页")]
      }),
      new Paragraph({
        children: [new TextRun("网站详情页展示网站的详细信息，包括网站描述、小节内容、评论列表等。用户可以点赞网站、发表评论。")]
      }),
      
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("5.2 管理后台实现")]
      }),
      new Paragraph({
        children: [new TextRun("管理后台采用Vue3和naive-ui组件库开发，主要实现以下功能：")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("5.2.1 登录功能")]
      }),
      new Paragraph({
        children: [new TextRun("管理员通过用户名和密码登录系统，系统验证身份后生成JWT令牌。")]
      }),
      new Paragraph({
        children: [new TextRun("图5-2 管理后台登录界面")]
      }),
      new Paragraph({
        children: [new ImageRun({
          type: "png",
          data: fs.readFileSync("../2026-06-11_20-56-48.png"),
          transformation: { width: 300, height: 600 },
          altText: { title: "管理后台侧边栏", description: "管理后台侧边栏界面截图", name: "侧边栏截图" }
        })]
      }),
      
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("5.2.2 网站管理")]
      }),
      new Paragraph({
        children: [new TextRun("管理员可以对网站进行增删改查操作，包括网站的基本信息、分类、标签等。")]
      }),
      
      // 代码示例
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun("5.2.3 核心代码示例")]
      }),
      new Paragraph({
        children: [new TextRun("以下为网站分页查询的核心代码：")]
      }),
      new Paragraph({
        children: [new TextRun({
          text: "@GetMapping(\"/page\")\npublic Result<PageResult<SiteVO>> page(SiteQueryDTO queryDTO) {\n    // 构建分页查询条件\n    Page<Site> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());\n    // 执行分页查询\n    Page<SiteVO> sitePage = siteService.getSitePage(page, queryDTO);\n    // 封装返回结果\n    PageResult<SiteVO> pageResult = new PageResult<>(sitePage.getRecords(), sitePage.getTotal());\n    return Result.success(pageResult);\n}",
          font: "Courier New",
          size: 20
        })]
      }),
      
      // 第六章 总结与展望
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("第六章 总结与展望")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("6.1 项目总结")]
      }),
      new Paragraph({
        children: [new TextRun("本项目成功实现了基于Spring Boot和Vue3的前后端分离网站导航系统，实现了网站管理、分类管理、用户管理、评论点赞等核心功能。系统架构清晰，代码结构规范，具有良好的可维护性和可扩展性。")]
      }),
      new Paragraph({
        children: [new TextRun("通过本次项目开发，我深入学习了Spring Boot、Vue3、MyBatis-Plus等主流开发技术，掌握了前后端分离的开发模式，提高了全栈开发能力。")]
      }),
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("6.2 项目展望")]
      }),
      new Paragraph({
        children: [new TextRun("本项目在功能上还有进一步完善的空间，未来可以在以下方面进行优化：")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("增加用户注册功能，支持更多用户交互方式")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("优化搜索算法，提高搜索准确性和响应速度")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("增加数据分析功能，为管理员提供更详细的统计信息")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("优化系统性能，支持更高并发访问")]
      }),
      new Paragraph({
        numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("增加移动端适配，提供更好的移动体验")]
      }),
      
      // 参考文献
      new Paragraph({ children: [new PageBreak()] }),
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun("参考文献")]
      }),
      [1, 2, 3, 4, 5].map(i => new Paragraph({
        numbering: { reference: "numbers", level: 0 },
        children: [new TextRun(`参考文献 ${i}`)]
      }))
    ]
  }]
});

// 生成文档
Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync("../专业实训V（JavaEE项目）设计报告书.docx", buffer);
  console.log("报告生成成功！");
}).catch(err => {
  console.error("生成报告时出错：", err);
});
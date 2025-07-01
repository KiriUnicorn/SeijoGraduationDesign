# 竞赛管理系统接口文档

## 目录
- [1. 认证模块](#1-认证模块)
- [2. 用户管理模块](#2-用户管理模块)
- [3. 竞赛管理模块](#3-竞赛管理模块)
- [4. 团队管理模块](#4-团队管理模块)
- [5. 报名管理模块](#5-报名管理模块)
- [6. 成绩管理模块](#6-成绩管理模块)
- [7. 公告管理模块](#7-公告管理模块)
- [8. 个人信息管理模块](#8-个人信息管理模块)
- [9. 通用响应格式](#9-通用响应格式)
- [10. 错误码说明](#10-错误码说明)

## 1. 认证模块

### 1.1 管理员登录
- **接口URL**: `/api/auth/admin/login`
- **请求方式**: POST
- **请求参数**:
```json
{
    "email": "string",     // 管理员邮箱（唯一登录凭证）
    "password": "string"   // 密码
}
```
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "string",
        "userInfo": {
            "id": 1,                    // 管理员ID
            "name": "张三",             // 管理员姓名
            "email": "zhangsan@example.com", // 邮箱
            "role": "admin"             // 角色固定为admin
        }
    }
}
```

### 1.2 教师登录
- **接口URL**: `/api/auth/teacher/login`
- **请求方式**: POST
- **请求参数**:
```json
{
    "email": "string",     // 教师邮箱（唯一登录凭证）
    "password": "string"   // 密码
}
```
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "string",
        "userInfo": {
            "id": 1,                    // 教师ID
            "name": "张三",             // 教师姓名
            "email": "zhangsan@example.com", // 邮箱
            "role": "teacher",          // 角色固定为teacher
            "departmentId": 1           // 所属院系ID
        }
    }
}
```

### 1.3 学生登录
- **接口URL**: `/api/auth/student/login`
- **请求方式**: POST
- **请求参数**:
```json
{
    "email": "string",     // 学生邮箱（唯一登录凭证）
    "password": "string"   // 密码
}
```
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "string",
        "userInfo": {
            "id": 1,                    // 学生ID
            "name": "张三",             // 学生姓名
            "email": "zhangsan@example.com", // 邮箱
            "role": "student",          // 角色固定为student
            "departmentId": 1           // 所属院系ID
        }
    }
}
```

### 1.4 获取管理员信息
- **接口URL**: `/api/auth/admin/info`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,                    // 管理员ID
        "name": "张三",             // 管理员姓名
        "email": "zhangsan@example.com" // 邮箱
    }
}
```

### 1.5 获取教师信息
- **接口URL**: `/api/auth/teacher/info`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,                    // 教师ID
        "name": "张三",             // 教师姓名
        "email": "zhangsan@example.com", // 邮箱
        "department": {
            "id": 1,
            "name": "计算机学院"
        }
    }
}
```

### 1.6 获取学生信息
- **接口URL**: `/api/auth/student/info`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,                    // 学生ID
        "name": "张三",             // 学生姓名
        "email": "zhangsan@example.com", // 邮箱
        "department": {
            "id": 1,
            "name": "计算机学院"
        }
    }
}
```

## 2. 用户管理模块

### 2.1 添加教师（管理员）
- **接口URL**: `/api/users/teachers`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",        // 教师姓名
    "email": "string",       // 教师邮箱（唯一登录账号）
    "password": "string",    // 初始密码
    "departmentId": 1        // 院系ID
}
```

### 2.2 添加学生（管理员）
- **接口URL**: `/api/users/students`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",        // 学生姓名
    "email": "string",       // 学生邮箱（唯一登录账号）
    "password": "string",    // 初始密码
    "departmentId": 1        // 院系ID
}
```

### 2.3 批量导入教师（管理员）
- **接口URL**: `/api/users/teachers/batch-import`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
  - Content-Type: multipart/form-data
- **请求参数**:
  - file: File (Excel文件，包含教师信息)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "success": 95,
        "failed": 5,
        "failedDetails": [
            {
                "row": 1,
                "reason": "邮箱格式错误"
            }
        ]
    }
}
```

### 2.4 批量导入学生（管理员）
- **接口URL**: `/api/users/students/batch-import`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
  - Content-Type: multipart/form-data
- **请求参数**:
  - file: File (Excel文件，包含学生信息)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "success": 95,
        "failed": 5,
        "failedDetails": [
            {
                "row": 1,
                "reason": "邮箱格式错误"
            }
        ]
    }
}
```

### 2.5 获取用户列表（管理员）
- **接口URL**: `/api/users`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
  - role: string (可选，teacher/student)
  - departmentId: number (可选)
  - keyword: string (可选，搜索关键词)
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,                    // 用户ID（teacher_id/student_id）
            "name": "张三",             // 用户姓名
            "email": "zhangsan@example.com", // 邮箱
            "role": "student",          // 角色：teacher/student
            "department": {
                "id": 1,
                "name": "计算机学院"
            }
        }]
    }
}
```

### 2.6 重置用户密码（管理员）
- **接口URL**: `/api/users/{id}/reset-password`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "newPassword": "string"  // 重置后的新密码
    }
}
```

### 2.7 删除用户（管理员）
- **接口URL**: `/api/users/{id}`
- **请求方式**: DELETE
- **请求头**: 
  - Authorization: Bearer {token}

## 3. 竞赛管理模块

### 3.1 创建竞赛
- **接口URL**: `/api/competitions`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "title": "string",           // 竞赛标题
    "description": "string",     // 竞赛描述
    "hostUnitId": 1,            // 主办单位ID
    "responsibleDepartmentId": 1, // 责任院系ID
    "levelId": 1,               // 竞赛等级ID
    "signupStartTime": "2024-03-01 00:00:00", // 报名开始时间
    "signupEndTime": "2024-03-15 00:00:00",   // 报名结束时间
    "competitionStartTime": "2024-04-01 00:00:00", // 竞赛开始时间
    "competitionEndTime": "2024-04-15 00:00:00"    // 竞赛结束时间
}
```

### 3.2 获取竞赛列表
- **接口URL**: `/api/competitions/findAll`
- **请求方式**: GET
- **请求参数**:
  - status: string (可选，pending/active/ended)
  - levelId: number (可选)
  - departmentId: number (可选)
  - keyword: string (可选，搜索关键词)
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "title": "string",
            "description": "string",
            "hostUnit": {
                "id": 1,
                "name": "string"
            },
            "department": {
                "id": 1,
                "name": "string"
            },
            "level": {
                "id": 1,
                "name": "string"
            },
            "signupStartTime": "2024-03-01 00:00:00",
            "signupEndTime": "2024-03-15 00:00:00",
            "competitionStartTime": "2024-04-01 00:00:00",
            "competitionEndTime": "2024-04-15 00:00:00",
            "status": "pending"
        }]
    }
}
```

### 3.3 获取竞赛详情
- **接口URL**: `/api/competitions/{id}`
- **请求方式**: GET
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "title": "string",
        "description": "string",
        "hostUnit": {
            "id": 1,
            "name": "string"
        },
        "department": {
            "id": 1,
            "name": "string"
        },
        "level": {
            "id": 1,
            "name": "string"
        },
        "signupStartTime": "2024-03-01 00:00:00",
        "signupEndTime": "2024-03-15 00:00:00",
        "competitionStartTime": "2024-04-01 00:00:00",
        "competitionEndTime": "2024-04-15 00:00:00",
        "status": "pending",
        "teams": [{
            "id": 1,
            "name": "string",
            "captain": {
                "id": 1,
                "name": "string"
            },
            "members": [{
                "id": 1,
                "name": "string"
            }]
        }]
    }
}
```

### 3.4 更新竞赛信息
- **接口URL**: `/api/competitions/{id}`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**: 同创建竞赛

### 3.5 删除竞赛
- **接口URL**: `/api/competitions/{id}`
- **请求方式**: DELETE
- **请求头**: 
  - Authorization: Bearer {token}

## 4. 团队管理模块

### 4.1 创建团队
- **接口URL**: `/api/teams`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",        // 团队名称
    "competitionId": 1,      // 竞赛ID
    "teacherId": 1,          // 指导教师ID
    "members": [1, 2, 3]     // 成员ID列表
}
```

### 4.2 获取团队列表
- **接口URL**: `/api/teams`
- **请求方式**: GET
- **请求参数**:
  - competitionId: number (可选)
  - teacherId: number (可选)
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "name": "string",
            "competition": {
                "id": 1,
                "title": "string"
            },
            "teacher": {
                "id": 1,
                "name": "string"
            },
            "captain": {
                "id": 1,
                "name": "string"
            },
            "members": [{
                "id": 1,
                "name": "string"
            }],
            "createdAt": "2024-03-01 00:00:00"
        }]
    }
}
```

### 4.3 更新团队信息
- **接口URL**: `/api/teams/{id}`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",        // 团队名称
    "teacherId": 1,          // 指导教师ID
    "members": [1, 2, 3]     // 成员ID列表
}
```

### 4.4 删除团队
- **接口URL**: `/api/teams/{id}`
- **请求方式**: DELETE
- **请求头**: 
  - Authorization: Bearer {token}

## 5. 报名管理模块

### 5.1 提交报名
- **接口URL**: `/api/registrations`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "teamId": 1,
    "competitionId": 1,
    "teacherId": 1          // 指导教师ID
}
```

### 5.2 获取报名列表
- **接口URL**: `/api/registrations`
- **请求方式**: GET
- **请求参数**:
  - status: string (可选，pending/approved/rejected)
  - competitionId: number (可选)
  - teacherId: number (可选)
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "team": {
                "id": 1,
                "name": "string"
            },
            "competition": {
                "id": 1,
                "title": "string"
            },
            "teacher": {
                "id": 1,
                "name": "string"
            },
            "status": "pending",
            "approver": {
                "id": 1,
                "name": "string"
            },
            "createdAt": "2024-03-01 00:00:00"
        }]
    }
}
```

### 5.3 审核报名
- **接口URL**: `/api/registrations/{id}/approve`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "status": "approved",    // approved/rejected
    "comment": "string"      // 审核意见
}
```

## 6. 成绩管理模块

### 6.1 录入成绩
- **接口URL**: `/api/scores`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "teamId": 1,
    "awardLevel": "一等奖",
    "certificatePath": "string"  // 证书文件路径
}
```

### 6.2 获取成绩列表
- **接口URL**: `/api/scores`
- **请求方式**: GET
- **请求参数**:
  - competitionId: number (可选)
  - awardLevel: string (可选)
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "team": {
                "id": 1,
                "name": "string"
            },
            "competition": {
                "id": 1,
                "title": "string"
            },
            "awardLevel": "一等奖",
            "certificatePath": "string",
            "teacher": {
                "id": 1,
                "name": "string"
            },
            "createdAt": "2024-03-01 00:00:00"
        }]
    }
}
```

### 6.3 更新成绩
- **接口URL**: `/api/scores/{id}`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "awardLevel": "一等奖",
    "certificatePath": "string"
}
```

## 7. 公告管理模块

### 7.1 发布公告
- **接口URL**: `/api/announcements`
- **请求方式**: POST
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "title": "string",
    "content": "string"
}
```

### 7.2 获取公告列表
- **接口URL**: `/api/announcements`
- **请求方式**: GET
- **请求参数**:
  - page: number (默认1)
  - pageSize: number (默认10)
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "title": "string",
            "content": "string",
            "admin": {
                "id": 1,
                "name": "string"
            },
            "createdAt": "2024-03-01 00:00:00"
        }]
    }
}
```

### 7.3 更新公告
- **接口URL**: `/api/announcements/{id}`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "title": "string",
    "content": "string"
}
```

### 7.4 删除公告
- **接口URL**: `/api/announcements/{id}`
- **请求方式**: DELETE
- **请求头**: 
  - Authorization: Bearer {token}

## 8. 个人信息管理模块

### 8.1 获取管理员个人信息
- **接口URL**: `/api/profile/admin`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "张三",
        "email": "zhangsan@example.com"
    }
}
```

### 8.2 获取教师个人信息
- **接口URL**: `/api/profile/teacher`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "张三",
        "email": "zhangsan@example.com",
        "department": {
            "id": 1,
            "name": "计算机学院"
        }
    }
}
```

### 8.3 获取学生个人信息
- **接口URL**: `/api/profile/student`
- **请求方式**: GET
- **请求头**: 
  - Authorization: Bearer {token}
- **响应结果**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "张三",
        "email": "zhangsan@example.com",
        "department": {
            "id": 1,
            "name": "计算机学院"
        }
    }
}
```

### 8.4 更新管理员个人信息
- **接口URL**: `/api/profile/admin`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",     // 姓名
    "email": "string"     // 邮箱
}
```

### 8.5 更新教师个人信息
- **接口URL**: `/api/profile/teacher`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",     // 姓名
    "email": "string"     // 邮箱
}
```

### 8.6 更新学生个人信息
- **接口URL**: `/api/profile/student`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "name": "string",     // 姓名
    "email": "string"     // 邮箱
}
```

### 8.7 修改管理员密码
- **接口URL**: `/api/profile/admin/password`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "oldPassword": "string",  // 旧密码
    "newPassword": "string"   // 新密码
}
```

### 8.8 修改教师密码
- **接口URL**: `/api/profile/teacher/password`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "oldPassword": "string",  // 旧密码
    "newPassword": "string"   // 新密码
}
```

### 8.9 修改学生密码
- **接口URL**: `/api/profile/student/password`
- **请求方式**: PUT
- **请求头**: 
  - Authorization: Bearer {token}
- **请求参数**:
```json
{
    "oldPassword": "string",  // 旧密码
    "newPassword": "string"   // 新密码
}
```

## 9. 通用响应格式

所有接口统一使用以下响应格式：
```json
{
    "code": number,      // 状态码：200成功，其他表示错误
    "message": "string", // 响应消息
    "data": object      // 响应数据
}
```

## 10. 错误码说明

- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误 
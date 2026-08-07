# Helloworld

Helloworld 是一个基于 Java 8 和 Spring Boot 2.7.7 的示例 REST API 服务，提供问候、健康检查、UTC 时间和实时天气查询接口。

## 核心功能

| 接口 | 说明 |
| --- | --- |
| `GET /demo/say/{name}` | 返回 `hello v2,{name}` 格式的问候文本 |
| `GET /healthz` | 返回服务健康状态 `{"status":"ok"}` |
| `GET /time` | 返回包含 UTC ISO-8601 时间的 JSON |
| `GET /weather?location={location}` | 通过 Open-Meteo 查询指定地点的当前天气 |

## 环境要求

- Java 8 或兼容版本
- 天气查询需要能够访问 Open-Meteo 的地理编码和天气服务

项目已包含 Maven Wrapper，无需单独安装 Maven。

## 快速开始

在项目根目录执行以下命令。

### Unix / macOS

先运行测试：

```bash
./mvnw test
```

再启动服务：

```bash
./mvnw spring-boot:run
```

### Windows

先运行测试：

```powershell
.\mvnw.cmd test
```

再启动服务：

```powershell
.\mvnw.cmd spring-boot:run
```

服务默认监听 `http://localhost:8080`。

## API 使用示例

启动服务后，可以在另一个终端中执行：

```bash
curl http://localhost:8080/healthz
curl http://localhost:8080/demo/say/Alice
curl http://localhost:8080/time
curl --get --data-urlencode "location=Shanghai" http://localhost:8080/weather
```

`location` 是天气接口的必填查询参数。使用 `--data-urlencode` 可以正确处理地点名称中的空格或非 ASCII 字符。

天气查询成功时会返回类似以下结构：

```json
{
  "location": {
    "name": "Shanghai",
    "country": "China",
    "latitude": 31.22,
    "longitude": 121.46
  },
  "condition": {
    "code": 1,
    "description": "mainly_clear"
  },
  "temperature": {
    "value": 28.4,
    "unit": "°C"
  },
  "observedAt": "2026-08-07T14:30:00Z"
}
```

以上响应仅用于展示字段结构。实际地点匹配、天气数据和观测时间来自上游实时服务。

天气接口可能返回以下错误状态：

- `400 Bad Request`：地点参数无效、为空或无法解析。
- `502 Bad Gateway`：上游天气服务不可用。
- `504 Gateway Timeout`：上游天气服务请求超时。

## 配置说明

默认配置位于 `src/main/resources/application.properties`：

```properties
server.port=8080
weather.provider.geocoding-base-url=https://geocoding-api.open-meteo.com
weather.provider.forecast-base-url=https://api.open-meteo.com
weather.provider.connect-timeout=2s
weather.provider.read-timeout=3s
```

这些属性可以通过标准 Spring Boot 配置方式覆盖，例如环境变量、命令行参数或外部配置文件。

## 项目结构

```text
.
├── src/main/java/        # 应用源码与 REST API 实现
├── src/main/resources/   # Spring Boot 配置
├── src/test/java/        # 单元测试和接口测试
├── cicd/                 # CI/CD 配置
├── mvnw                  # Unix/macOS Maven Wrapper
├── mvnw.cmd              # Windows Maven Wrapper
└── pom.xml               # Maven 项目配置
```

## 测试

运行完整测试套件：

```bash
./mvnw test
```

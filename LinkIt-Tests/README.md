# LinkIt Tests

Standalone automation project for LinkIt backend API + frontend UI testing.

## Tech Stack
- TestNG
- Rest Assured (API)
- Selenium + WebDriverManager (UI)

## Covered Scenarios
### Backend API
- Create room with defaults
- Room ID uniqueness
- Join valid room
- Join invalid room (204)
- Get content
- Get participants
- Get file names
- Remove last participant deletes room
- Unknown-room behaviors

### Frontend UI
- Home page controls render
- Join tab reveals room ID input
- Create room navigation to `/room/:roomId`
- Join existing room flow
- Invalid join toast error
- Deep-link room route

## Prerequisites
1. Start backend (`LinkitBackend`) on `http://localhost:8080`
2. Start frontend (`LinkIt-Frontend`) on `http://localhost:5173`
3. JDK 17+
4. Chrome or Edge installed

## Run tests
```bash
cd LinkIt-Tests
mvn test
```

## Optional runtime overrides
```bash
cd LinkIt-Tests
mvn test -DHEADLESS=true -DBROWSER=edge -DLINKIT_API_BASE_URL=http://localhost:8080 -DLINKIT_UI_BASE_URL=http://localhost:5173
```

## Notes
- API/UI tests auto-skip when target service is not reachable.
- Tests create temporary rooms and clean up participants after each test.


# Contributing to Contact Management System

Thank you for considering a contribution to the Contact Management System! Here are guidelines to help you get started.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the code, not the person
- Help others learn and grow

## How to Contribute

### Reporting Bugs

1. **Check existing issues** - Make sure the bug hasn't been reported
2. **Provide details:**
   - Clear, descriptive title
   - Detailed description of the issue
   - Steps to reproduce
   - Expected vs. actual behavior
   - Screenshots if applicable
   - Your environment (OS, Java version, Node version, etc.)

### Suggesting Features

1. **Check discussions** - See if it's already been discussed
2. **Provide context:**
   - Use case and why it's needed
   - Proposed solution or API design
   - Alternative approaches considered
   - Any drawbacks or implications

### Pull Requests

#### Before Starting

1. Fork the repository
2. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Keep your branch updated with main

#### Development Process

1. **Code Quality:**
   - Follow project's code style
   - Write clean, readable code
   - Add comments for complex logic
   - No dead code or console.logs

2. **Testing:**
   - Write unit tests for new features
   - Ensure all tests pass: `mvn test`
   - Aim for > 80% code coverage
   - Test edge cases and error scenarios

3. **Backend Guidelines:**
   - Follow Spring Boot conventions
   - Use appropriate HTTP status codes
   - Validate all inputs
   - Handle exceptions gracefully
   - Document complex methods with JavaDoc
   - Use dependency injection
   - Follow REST principles

4. **Frontend Guidelines:**
   - Use functional components with hooks
   - Keep components small and reusable
   - Use meaningful variable and function names
   - Add PropTypes or TypeScript types
   - Test user interactions
   - Ensure responsive design

#### Commit Guidelines

- Use clear, descriptive commit messages
- Follow conventional commits format:
  ```
  type(scope): subject

  body (optional)

  footer (optional)
  ```
- Types: feat, fix, docs, style, refactor, perf, test, chore
- Example:
  ```
  feat(contacts): add bulk delete functionality

  - Implemented multi-select checkbox
  - Added confirmation modal
  - Optimized batch delete query
  
  Closes #123
  ```

#### Submitting PR

1. Push to your fork
2. Create Pull Request with:
   - Clear title describing changes
   - Description of what changed and why
   - Reference to related issues (#123)
   - Screenshots for UI changes
   - Checklist of testing performed

3. PR Template:
   ```markdown
   ## Description
   Brief description of changes

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Breaking change
   - [ ] Documentation update

   ## Changes Made
   - Change 1
   - Change 2

   ## Testing Performed
   - [ ] Unit tests added/updated
   - [ ] Integration tested
   - [ ] Tested on different browsers/devices

   ## Checklist
   - [ ] Code follows style guidelines
   - [ ] Self-review completed
   - [ ] Comments added for complex code
   - [ ] Documentation updated
   - [ ] No new warnings generated
   - [ ] Tests pass locally
   ```

## Development Setup

See [QUICKSTART.md](./QUICKSTART.md) for setup instructions.

## Code Style Guide

### Java (Backend)

```java
// Class naming: PascalCase
public class UserService {
    
    // Method naming: camelCase
    public User getUserById(Long id) {
        // Implementation
    }
    
    // Constants: UPPER_SNAKE_CASE
    private static final String DEFAULT_ROLE = "USER";
    
    // Variable naming: camelCase
    private String userName;
}
```

- Use 4-space indentation
- Max line length: 120 characters
- Use meaningful names
- One statement per line

### JavaScript/React (Frontend)

```javascript
// Component naming: PascalCase
export const ContactForm = ({ contact, onSave }) => {
  // Hooks at top
  const [formData, setFormData] = useState({});
  
  // Handler functions
  const handleSubmit = (e) => {
    e.preventDefault();
    // Implementation
  };
  
  // JSX
  return (
    <form onSubmit={handleSubmit}>
      {/* JSX content */}
    </form>
  );
};

// Helper naming: camelCase
export const formatDate = (date) => {
  // Implementation
};

// Constants: UPPER_SNAKE_CASE
const API_BASE_URL = 'http://localhost:8080/api';
```

- Use 2-space indentation
- Use arrow functions
- Destructure props
- Use meaningful component names

## Testing Standards

### Backend Tests

```java
@Test
void testFeatureSuccess() {
    // Arrange - setup test data
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Act - execute function
    Result result = service.getEntity(1L);
    
    // Assert - verify results
    assertNotNull(result);
    assertEquals("expected", result.getValue());
    verify(repository, times(1)).findById(1L);
}

@Test
void testFeatureError() {
    // Test error handling
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getEntity(1L));
}
```

- Test name: `test[Feature][Scenario]`
- Follow Arrange-Act-Assert pattern
- Test both success and error cases
- Use meaningful assertions

### Frontend Tests

```javascript
describe('ContactForm', () => {
  it('should submit form with valid data', () => {
    render(<ContactForm onSave={mockFn} />);
    
    fireEvent.change(screen.getByName('firstName'), { target: { value: 'John' } });
    fireEvent.click(screen.getByText('Save'));
    
    expect(mockFn).toHaveBeenCalled();
  });
});
```

## Performance Considerations

1. **Backend:**
   - Minimize database queries
   - Use pagination for large datasets
   - Cache frequently accessed data
   - Optimize N+1 query problems

2. **Frontend:**
   - Lazy load routes and components
   - Memoize expensive computations
   - Avoid unnecessary re-renders
   - Optimize images and assets

## Security Checklist

- [ ] No hardcoded secrets
- [ ] Validate all user inputs
- [ ] Use parameterized queries
- [ ] Implement proper authorization
- [ ] Use HTTPS in production
- [ ] Follow OWASP guidelines
- [ ] Sanitize output
- [ ] Keep dependencies updated

## Documentation

- Update README if behavior changes
- Add comments for complex logic
- Document public APIs with JavaDoc/JSDoc
- Update QUICKSTART.md for setup changes
- Add examples for new features

## Review Process

1. Code review by maintainer
2. Automated tests must pass
3. SonarQube quality gates must pass
4. Minimum 1 approval required
5. Branch must be up to date with main
6. CI/CD pipeline must succeed

## Release Process

- Semantic versioning (MAJOR.MINOR.PATCH)
- Update CHANGELOG.md
- Tag release in Git
- Publish to package registry

## Questions or Need Help?

- Check existing issues and discussions
- Comment on relevant issues
- Start a discussion for general questions
- Email maintainers if needed

## Thank You!

Your contributions help make this project better for everyone!

---

**Last Updated:** August 2026

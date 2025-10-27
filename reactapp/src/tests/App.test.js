import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';
import * as blogService from '../services/blogService';
import * as commentService from '../services/commentService';
import * as reportService from '../services/reportService';
import "@testing-library/jest-dom"
jest.mock('../services/blogService');
jest.mock('../services/commentService');
jest.mock('../services/reportService');

const mockBlogs = [
  { id: 1, title: 'First Blog', content: '# Hello World' },
  { id: 2, title: 'Second Blog', content: 'Some content here' }
];

const mockComments = [
  { id: 1, text: 'Nice post!' },
  { id: 2, text: 'Thanks for sharing' }
];

const mockReports = [
  {
    id: 1,
    reason: 'Inappropriate content',
    blog: { id: 1, title: 'Reported Blog', content: 'Some content' }
  }
];

describe('Multi-User Blogging Platform - Integration Tests', () => {
  beforeEach(() => {
    blogService.getAllBlogs.mockResolvedValue(mockBlogs);
    blogService.createBlog.mockResolvedValue({ success: true });
    commentService.getComments.mockResolvedValue(mockComments);
    commentService.postComment.mockResolvedValue({ success: true });
    reportService.getReports.mockResolvedValue(mockReports);
    reportService.deleteBlog.mockResolvedValue({ success: true });
  });


  test('React_BuildUIComponents_renders main heading and fetches blogs', async () => {
    render(<App />);
    expect(screen.getByText(/Multi-User Blogging Platform/i)).toBeInTheDocument();
    await waitFor(() => expect(blogService.getAllBlogs).toHaveBeenCalled());
  });


  test('React_APIIntegration_TestingAndAPIDocumentation_displays blog titles in BlogList', async () => {
    render(<App />);
    expect(await screen.findByText('First Blog')).toBeInTheDocument();
    expect(screen.getByText('Second Blog')).toBeInTheDocument();
  });


  test('React_BuildUIComponents_renders blog editor input fields and updates them', () => {
    render(<App />);
    const titleInput = screen.getByPlaceholderText(/Blog Title/i);
    const contentInput = screen.getByPlaceholderText(/Write in Markdown/i);

    fireEvent.change(titleInput, { target: { value: 'My Blog' } });
    fireEvent.change(contentInput, { target: { value: 'Hello world' } });

    expect(titleInput.value).toBe('My Blog');
    expect(contentInput.value).toBe('Hello world');
  });


  test('React_APIIntegration_TestingAndAPIDocumentation_submits blog and refreshes list', async () => {
    render(<App />);

    fireEvent.change(screen.getByPlaceholderText(/Blog Title/i), { target: { value: 'New Blog' } });
    fireEvent.change(screen.getByPlaceholderText(/Write in Markdown/i), { target: { value: 'Some content' } });
    fireEvent.click(screen.getByRole('button', { name: /Post/i }));

    await waitFor(() => expect(blogService.createBlog).toHaveBeenCalled());
    await waitFor(() => expect(blogService.getAllBlogs).toHaveBeenCalledTimes(2));
  });

  


  test('React_UITestingAndResponsivenessFixes_displays selected blog content in BlogViewer', async () => {
    render(<App />);
    const blog = await screen.findByText('First Blog');
    fireEvent.click(blog);
    expect(await screen.findByText(/Hello World/i)).toBeInTheDocument();
  });

  test('React_UITestingAndResponsivenessFixes_blog content display', async () => {
    render(<App />);
    const blog = await screen.findByText('First Blog');
    fireEvent.click(blog);
    expect(await screen.findByText(/Hello World/i)).toBeInTheDocument();
  });

  test('React_APIIntegration_TestingAndAPIDocumentation_displays comments for selected blog', async () => {
    render(<App />);
    fireEvent.click(await screen.findByText('First Blog'));
    expect(await screen.findByText('Nice post!')).toBeInTheDocument();
    expect(screen.getByText('Thanks for sharing')).toBeInTheDocument();
  });

  // ➕ NEW TEST 1

  test('React_APIIntegration_TestingAndAPIDocumentation_allows user to post a comment', async () => {
    render(<App />);
    fireEvent.click(await screen.findByText('First Blog'));

    const input = screen.getByPlaceholderText(/Add a comment/i);
    fireEvent.change(input, { target: { value: 'Great blog!' } });
    fireEvent.click(screen.getByRole('button', { name: /Comment/i }));

    await waitFor(() => expect(commentService.postComment).toHaveBeenCalledWith(1, 'Great blog!'));
  });
  // ➕ NEW TEST 3

  test('React_UITestingAndResponsivenessFixes_switches between blogs and shows correct content', async () => {
    render(<App />);
    fireEvent.click(await screen.findByText('First Blog'));
    expect(await screen.findByText(/Hello World/i)).toBeInTheDocument();

    fireEvent.click(screen.getByText('Second Blog'));
    expect(await screen.findByText(/Some content here/i)).toBeInTheDocument();
  });

  // ➕ NEW TEST 4

  test('React_BuildUIComponents_renders markdown content as HTML in BlogViewer', async () => {
    render(<App />);
    fireEvent.click(await screen.findByText('First Blog'));

    const heading = await screen.findByRole('heading', { name: /Hello World/i });
    expect(heading).toBeInTheDocument(); // markdown `# Hello World` should render as <h1>
  });

  
});

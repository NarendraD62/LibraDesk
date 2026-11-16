import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import App, { calculateMolarMass } from './App';

vi.mock('./lib/api', () => ({
  searchBooks: async () => [{ id: 10, title: 'Sample Algorithms', author: 'Demo Author', isbn: '000', category: 'Computer Science', year: 2024, available: 1, popularity: 10, description: 'Synthetic.', shelfLocation: 'CS-A1' }],
  suggestBooks: async () => ['Sample Algorithms'],
}));
afterEach(cleanup);
beforeEach(() => window.localStorage.clear());

describe('catalog search', () => {
  it('shows results and lets a reader choose a live suggestion', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><App /></QueryClientProvider>);
    expect(await screen.findByRole('button', { name: 'Sample Algorithms' })).toBeTruthy();
    fireEvent.change(screen.getByRole('textbox', { name: 'Search the catalog' }), { target: { value: 'sam' } });
    await waitFor(() => expect(screen.getAllByRole('button', { name: 'Sample Algorithms' }).length).toBeGreaterThan(1));
  });

  it('saves books and lists them on the Saved books page', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><App /></QueryClientProvider>);
    await screen.findByRole('button', { name: 'Sample Algorithms' });
    fireEvent.click(screen.getByRole('button', { name: 'Save Sample Algorithms' }));
    fireEvent.click(screen.getByRole('button', { name: /Saved books/ }));
    expect(await screen.findByText(/Shelf CS-A1/)).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Remove Sample Algorithms from saved books' }));
    expect(await screen.findByText('No saved books yet')).toBeTruthy();
  });

  it('shows physical shelf directions on the book detail page', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><App /></QueryClientProvider>);
    fireEvent.click(await screen.findByRole('button', { name: 'Sample Algorithms' }));
    expect(await screen.findByRole('heading', { name: 'Find it in the library' })).toBeTruthy();
    expect(screen.getByText('CS-A1')).toBeTruthy();
    expect(screen.getByLabelText(/aisle A, shelf 1/)).toBeTruthy();
  });

  it('borrows a book with borrower details and a live rent total', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><App /></QueryClientProvider>);
    fireEvent.click(await screen.findByRole('button', { name: 'Borrow Sample Algorithms' }));
    fireEvent.change(screen.getByLabelText('Borrower name'), { target: { value: 'Asha Reader' } });
    fireEvent.change(screen.getByRole('textbox', { name: /Mobile number/ }), { target: { value: '9876543210' } });
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Borrow duration in days' }), { target: { value: '10' } });
    expect(screen.getByRole('button', { name: 'Confirm borrow · ₹50' })).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Confirm borrow · ₹50' }));
    expect(await screen.findByRole('heading', { name: 'Borrowed books' })).toBeTruthy();
    expect(screen.getByText('Asha Reader')).toBeTruthy();
    expect(screen.getByText('₹50')).toBeTruthy();
  });

  it('calculates common formula masses and rejects unknown elements', () => {
    expect(calculateMolarMass('H2O')).toBeCloseTo(18.015, 3);
    expect(calculateMolarMass('Ca(OH)2')).toBeCloseTo(74.092, 3);
    expect(calculateMolarMass('Fe2O3')).toBeCloseTo(159.687, 3);
    expect(calculateMolarMass('CuSO4')).toBeCloseTo(159.602, 3);
    expect(calculateMolarMass('Xx2')).toBeNull();
  });

  it('opens the Chemistry Lab and updates the formula tools', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><App /></QueryClientProvider>);
    fireEvent.click(screen.getByRole('button', { name: 'Chemistry Lab' }));
    expect(await screen.findByRole('heading', { name: 'Periodic table explorer' })).toBeTruthy();
    expect(screen.getAllByRole('button', { name: /atomic number/ }).length).toBe(118);
    fireEvent.click(screen.getByRole('button', { name: 'Oganesson, atomic number 118' }));
    expect(screen.getByRole('heading', { name: 'Oganesson' })).toBeTruthy();
    expect(screen.getByText('18.015 g/mol')).toBeTruthy();
    fireEvent.change(screen.getByRole('textbox', { name: 'Chemical formula' }), { target: { value: 'Ca(OH)2' } });
    expect(screen.getByText('74.092 g/mol')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Calcium, atomic number 20' }));
    expect(screen.getByText('Calcium')).toBeTruthy();
  });
});

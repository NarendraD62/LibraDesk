export type Book = { id: number; title: string; author: string; isbn: string; category: string; year: number; available: number; popularity: number; description: string; shelfLocation: string };
const base = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';
const demo: Book[] = [
  { id: 1, title: 'The Algorithm Atlas', author: 'Mira Sen', isbn: '9780000000001', category: 'Computer Science', year: 2023, available: 2, popularity: 91, description: 'A friendly map of core algorithms and the ideas behind them.', shelfLocation: 'CS-A1' },
  { id: 2, title: 'Gardens of the Monsoon', author: 'Asha Raman', isbn: '9780000000002', category: 'Literature', year: 2021, available: 1, popularity: 76, description: 'Stories of family, memory, and a changing city.', shelfLocation: 'LIT-B2' },
  { id: 3, title: 'A Brief History of Stars', author: 'Dev Kapoor', isbn: '9780000000003', category: 'Science', year: 2024, available: 3, popularity: 68, description: 'An illustrated introduction to the night sky.', shelfLocation: 'SCI-C1' },
  { id: 4, title: 'Designing Small Systems', author: 'Nina Das', isbn: '9780000000004', category: 'Computer Science', year: 2022, available: 0, popularity: 57, description: 'Practical patterns for building clear and resilient software.', shelfLocation: 'CS-A2' },
  { id: 5, title: 'The Quiet Archive', author: 'Kabir Iyer', isbn: '9780000000005', category: 'History', year: 2020, available: 2, popularity: 42, description: 'How communities preserve the stories they inherit.', shelfLocation: 'HIS-D1' },
  { id: 6, title: 'Maps of Salt and Wind', author: 'Tara Menon', isbn: '9780000000006', category: 'Literature', year: 2023, available: 1, popularity: 38, description: 'A coastal family follows old maps through a changing landscape.', shelfLocation: 'LIT-C1' },
  { id: 7, title: 'Practical Machine Learning', author: 'Arjun Mehta', isbn: '9780000000007', category: 'Computer Science', year: 2024, available: 2, popularity: 82, description: 'A hands-on introduction to learning systems and thoughtful evaluation.', shelfLocation: 'CS-B3' },
  { id: 8, title: 'The Garden of Small Worlds', author: 'Tara Bose', isbn: '9780000000008', category: 'Science', year: 2019, available: 1, popularity: 35, description: 'Discovering ecology through the tiny habitats around us.', shelfLocation: 'SCI-A2' },
  { id: 9, title: 'Cities Before Dawn', author: 'Leela Mukherjee', isbn: '9780000000009', category: 'History', year: 2018, available: 2, popularity: 31, description: 'How trade, craft, and night work shaped historic cities.', shelfLocation: 'HIS-A4' },
  { id: 10, title: 'Learning TypeScript', author: 'Omar Khan', isbn: '9780000000010', category: 'Computer Science', year: 2025, available: 3, popularity: 49, description: 'Build safer web applications with a practical TypeScript guide.', shelfLocation: 'CS-B1' },
  { id: 11, title: 'The River Between Us', author: 'Ria Dutta', isbn: '9780000000011', category: 'Literature', year: 2022, available: 0, popularity: 46, description: 'Two neighbors tell the story of a river and the town it joins.', shelfLocation: 'LIT-A3' },
  { id: 12, title: 'Patterns in Nature', author: 'Ishaan Roy', isbn: '9780000000012', category: 'Science', year: 2023, available: 1, popularity: 52, description: 'A visual tour of repeating forms in plants, weather, and minerals.', shelfLocation: 'SCI-C2' },
];
export type LabResult = { method: string; count: number; micros: number };
export type LabStats = { catalogBooks: number; titleIndex: string; authorIndex: string; wordIndex: string; orderedIndex: string };
export async function searchBooks(query: string): Promise<Book[]> {
  if (import.meta.env.VITE_USE_MOCK === 'true') return demo.filter((b) => `${b.title} ${b.author} ${b.category}`.toLowerCase().includes(query.toLowerCase()));
  const response = await fetch(`${base}/books/search?q=${encodeURIComponent(query)}`);
  if (!response.ok) throw new Error('Could not load the catalog.');
  const result = await response.json() as Book[] | { items: Book[] };
  return Array.isArray(result) ? result : result.items;
}
export async function suggestBooks(prefix: string): Promise<string[]> {
  if (import.meta.env.VITE_USE_MOCK === 'true') return demo.flatMap((b) => [b.title, b.author]).filter((s) => s.toLowerCase().startsWith(prefix.toLowerCase())).slice(0, 6);
  const response = await fetch(`${base}/books/suggest?prefix=${encodeURIComponent(prefix)}`);
  if (!response.ok) throw new Error('Could not load suggestions.');
  return response.json() as Promise<string[]>;
}
export async function compareSearch(query: string): Promise<LabResult[]> {
  if (import.meta.env.VITE_USE_MOCK === 'true') return [{ method: 'Inverted word index AND', count: demo.filter((b) => `${b.title} ${b.author}`.toLowerCase().includes(query.toLowerCase())).length, micros: 34 }, { method: 'AVL title prefix scan', count: demo.filter((b) => b.title.toLowerCase().startsWith(query.toLowerCase())).length, micros: 61 }, { method: 'Linear substring scan', count: demo.filter((b) => `${b.title} ${b.author}`.toLowerCase().includes(query.toLowerCase())).length, micros: 118 }];
  const response = await fetch(`${base}/lab/search-compare?q=${encodeURIComponent(query)}`);
  if (!response.ok) throw new Error('Could not load search comparison.');
  const data = await response.json() as { results: LabResult[] };
  return data.results;
}
export async function getLabStats(): Promise<LabStats> {
  if (import.meta.env.VITE_USE_MOCK === 'true') return { catalogBooks: demo.length, titleIndex: 'Trie', authorIndex: 'Trie', wordIndex: 'InvertedWordIndex', orderedIndex: 'AVL tree' };
  const response = await fetch(`${base}/lab/structure-stats`);
  if (!response.ok) throw new Error('Could not load structure statistics.');
  return response.json() as Promise<LabStats>;
}

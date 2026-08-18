import React, { useState } from 'react';
import Navbar from '../components/Navbar';
import { BookOpen, CheckCircle, RotateCw, Layers, Cpu, Database, Globe, Server } from 'lucide-react';

const FLASHCARDS_DATA = [
  // Operating Systems
  {
    id: 1,
    topic: 'Operating Systems',
    question: 'What is the main difference between a Process and a Thread?',
    answer: 'A Process is an independent executing program with its own memory address space allocated by the OS. A Thread is a lightweight segment within a process that shares memory and resources with other threads of the same process, enabling fast context switching.',
    difficulty: 'EASY',
    icon: Cpu
  },
  {
    id: 2,
    topic: 'Operating Systems',
    question: 'What are the 4 necessary conditions for a Deadlock to occur?',
    answer: '1. Mutual Exclusion (non-shareable resources)\n2. Hold and Wait (process holds resource while waiting for another)\n3. No Preemption (resource cannot be forcibly taken)\n4. Circular Wait (a closed chain of processes waiting for each other).',
    difficulty: 'MEDIUM',
    icon: Cpu
  },
  {
    id: 3,
    topic: 'Operating Systems',
    question: 'What is Virtual Memory and how does Paging work?',
    answer: 'Virtual memory allows execution of processes larger than physical RAM by mapping virtual addresses to physical frames. Paging divides physical RAM into fixed-size blocks (frames) and virtual memory into pages. Page tables translate virtual addresses to physical frame addresses via the MMU.',
    difficulty: 'MEDIUM',
    icon: Cpu
  },

  // DBMS & SQL
  {
    id: 4,
    topic: 'DBMS',
    question: 'Explain the ACID properties in relational databases.',
    answer: '• Atomicity: All operations in a transaction succeed or all fail (roll back).\n• Consistency: Database transitions from one valid state to another.\n• Isolation: Concurrent transactions do not interfere with each other.\n• Durability: Committed transactions persist permanently even during system crashes.',
    difficulty: 'EASY',
    icon: Database
  },
  {
    id: 5,
    topic: 'DBMS',
    question: 'How does Database Indexing work (B-Tree vs Hash Index)?',
    answer: 'Indexes speed up retrieval by creating self-balancing data structures. B-Tree indexes store sorted keys supporting equality and range queries O(log N). Hash indexes use a hash table offering O(1) exact-match lookups but do not support range queries (<, >).',
    difficulty: 'MEDIUM',
    icon: Database
  },
  {
    id: 6,
    topic: 'DBMS',
    question: 'What are the Database Normalization forms (1NF, 2NF, 3NF)?',
    answer: '• 1NF: Atomic values only (no multi-valued columns).\n• 2NF: In 1NF + no partial dependency (non-prime attributes depend on full primary key).\n• 3NF: In 2NF + no transitive dependency (non-prime attributes depend only on primary key).',
    difficulty: 'MEDIUM',
    icon: Database
  },

  // Computer Networks
  {
    id: 7,
    topic: 'Computer Networks',
    question: 'Compare TCP vs UDP protocols.',
    answer: '• TCP (Transmission Control Protocol): Connection-oriented, reliable, guarantees ordered delivery, error-checking, flow control (3-way handshake). Used for Web (HTTP), Email (SMTP).\n• UDP (User Datagram Protocol): Connectionless, fast, lightweight, no delivery guarantee. Used for Gaming, Video Streaming, DNS.',
    difficulty: 'EASY',
    icon: Globe
  },
  {
    id: 8,
    topic: 'Computer Networks',
    question: 'What happens during a TCP 3-Way Handshake?',
    answer: '1. SYN: Client sends a packet with SYN flag and initial sequence number (ISN_A) to Server.\n2. SYN-ACK: Server responds with SYN-ACK, acknowledging ISN_A + 1 and sending its ISN_B.\n3. ACK: Client sends ACK acknowledging ISN_B + 1. Connection is established.',
    difficulty: 'MEDIUM',
    icon: Globe
  },
  {
    id: 9,
    topic: 'Computer Networks',
    question: 'Explain the steps involved in a DNS Lookup.',
    answer: '1. Browser checks local DNS cache.\n2. Queries Recursive Resolver (ISP).\n3. Queries Root Nameserver (.) -> Returns TLD server (.com).\n4. Queries TLD Nameserver -> Returns Authoritative Nameserver.\n5. Authoritative Nameserver returns IP address to resolver and client.',
    difficulty: 'MEDIUM',
    icon: Globe
  },

  // System Design
  {
    id: 10,
    topic: 'System Design',
    question: 'Explain the CAP Theorem in Distributed Systems.',
    answer: 'In any distributed data store, you can only guarantee 2 out of 3 properties simultaneously during a network partition:\n• Consistency (Every read receives the latest write or error)\n• Availability (Every request receives a non-error response)\n• Partition Tolerance (System operates despite network delays/losses).',
    difficulty: 'MEDIUM',
    icon: Server
  },
  {
    id: 11,
    topic: 'System Design',
    question: 'What is Consistent Hashing and why is it used?',
    answer: 'Consistent Hashing distributes keys across server nodes mapped onto a conceptual hash ring (0 to 2^32-1). When servers scale up or down, only K/N keys are remapped (where K is total keys and N is total servers), preventing massive cache invalidations.',
    difficulty: 'HARD',
    icon: Server
  },
  {
    id: 12,
    topic: 'System Design',
    question: 'What are Caching Strategies (Write-Through vs Write-Back)?',
    answer: '• Cache-Aside: App reads from cache; on miss, reads DB and populates cache.\n• Write-Through: App writes to cache; cache synchronously writes to DB.\n• Write-Back: App writes to cache; cache asynchronously writes to DB in batches (high throughput, risk of data loss on crash).',
    difficulty: 'HARD',
    icon: Server
  }
];

const CoreCsFlashcards = () => {
  const [selectedTopic, setSelectedTopic] = useState('All');
  const [flippedCardId, setFlippedCardId] = useState(null);
  const [masteredCards, setMasteredCards] = useState(new Set());

  const filteredCards = selectedTopic === 'All'
    ? FLASHCARDS_DATA
    : FLASHCARDS_DATA.filter((card) => card.topic === selectedTopic);

  const toggleMastered = (id, e) => {
    e.stopPropagation();
    setMasteredCards((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header">
          <div>
            <h1 className="page-title">Core CS & System Design Revision</h1>
            <p className="page-subtitle">Interactive 3D flashcards for OS, DBMS, Computer Networks, and High-Level System Design</p>
          </div>
          <div className="flashcard-stats-badge">
            <CheckCircle size={16} style={{ color: '#10b981' }} />
            <span>{masteredCards.size} of {FLASHCARDS_DATA.length} Concepts Mastered</span>
          </div>
        </div>

        {/* Topic Filter Tabs */}
        <div className="difficulty-tabs" style={{ marginBottom: '1.75rem' }}>
          {['All', 'Operating Systems', 'DBMS', 'Computer Networks', 'System Design'].map((topic) => (
            <button
              key={topic}
              className={`tab-btn ${selectedTopic === topic ? 'active' : ''}`}
              onClick={() => { setSelectedTopic(topic); setFlippedCardId(null); }}
            >
              {topic}
            </button>
          ))}
        </div>

        {/* 3D Flashcard Grid */}
        <div className="flashcards-grid">
          {filteredCards.map((card) => {
            const isFlipped = flippedCardId === card.id;
            const isMastered = masteredCards.has(card.id);
            const Icon = card.icon;

            return (
              <div
                key={card.id}
                className={`flashcard-scene ${isFlipped ? 'flipped' : ''}`}
                onClick={() => setFlippedCardId(isFlipped ? null : card.id)}
              >
                <div className="flashcard-inner">
                  {/* Card Front */}
                  <div className={`flashcard-face front ${isMastered ? 'mastered' : ''}`}>
                    <div className="card-top-row">
                      <span className="card-topic-tag">
                        <Icon size={13} style={{ marginRight: '4px' }} />
                        {card.topic}
                      </span>
                      <span className={`badge badge-${card.difficulty.toLowerCase()}`}>
                        {card.difficulty}
                      </span>
                    </div>

                    <div className="card-question-body">
                      <h3>{card.question}</h3>
                    </div>

                    <div className="card-bottom-row">
                      <span className="flip-hint"><RotateCw size={13} /> Click card to flip answer</span>
                      <button
                        className={`master-btn ${isMastered ? 'active' : ''}`}
                        onClick={(e) => toggleMastered(card.id, e)}
                      >
                        <CheckCircle size={14} /> {isMastered ? 'Mastered' : 'Mark Mastered'}
                      </button>
                    </div>
                  </div>

                  {/* Card Back */}
                  <div className="flashcard-face back">
                    <div className="card-top-row">
                      <span className="card-topic-tag answer">Key Concepts & Answer</span>
                    </div>

                    <div className="card-answer-body">
                      <p>{card.answer}</p>
                    </div>

                    <div className="card-bottom-row">
                      <span className="flip-hint"><RotateCw size={13} /> Click to flip back</span>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};

export default CoreCsFlashcards;
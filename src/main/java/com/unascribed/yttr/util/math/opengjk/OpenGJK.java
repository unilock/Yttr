package com.unascribed.yttr.util.math.opengjk;

/**
 * @file openGJK.c
 * @author Mattia Montanari
 * @date 1 Jan 2022
 * @brief Source of OpenGJK and its fast sub-algorithm.
 *
 * @see "https://www.mattiamontanari.com/opengjk/"
 */
public class OpenGJK {

private static int bool2int(boolean b) { return b ? 1 : 0; }
private static boolean int2bool(int i) { return i != 0; }

//                           _____      _ _  __                                   //
//                          / ____|    | | |/ /                                   //
//    ___  _ __   ___ _ __ | |  __     | | ' /                                    //
//   / _ \| '_ \ / _ \ '_ \| | |_ |_   | |  <                                     //
//  | (_) | |_) |  __/ | | | |__| | |__| | . \                                    //
//   \___/| .__/ \___|_| |_|\_____|\____/|_|\_\                                   //
//        | |                                                                     //
//        |_|                                                                     //
//                                                                                //
// Copyright 2022 Mattia Montanari, University of Oxford                          //
//                                                                                //
// This program is free software: you can redistribute it and/or modify it under  //
// the terms of the GNU General Public License as published by the Free Software  //
// Foundation, either version 3 of the License. You should have received a copy   //
// of the GNU General Public License along with this program. If not, visit       //
//                                                                                //
//     https://www.gnu.org/licenses/                                              //
//                                                                                //
// This program is distributed in the hope that it will be useful, but WITHOUT    //
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  //
// FOR A PARTICULAR PURPOSE. See GNU General Public License for details.          //

/** Data structure for convex polytopes.
*
* Polytopes are three-dimensional shapes and the GJK algorithm works directly on their convex-hull. However the convex-hull is never computed explicitly, instead each GJK-iteration employs a support function that has a cost linearly dependent on the number of points defining the polytope. */
public static class Polytope {
	public double[] s = new double[3]; /*!< Furthest point returned by the support function and updated at each GJK-iteration. For the first iteration this value is a guess - and this guess not irrelevant. */
	public double[][]
	   coord; /*!< Coordinates of the points of the polytope. This is owned by user who manages and garbage-collects the memory for these coordinates. */
}

/*! @brief Data structure for simplex.
*
* The simplex is updated at each GJK-iteration. For the first iteration this value is a guess - and this guess not irrelevant. */
public static class Simplex {
	public int nvrtx;          /*!< Number of points defining the simplex. */
	public double[][] vrtx = new double[4][3]; /*!< Coordinates of the points of the simplex. */
}
	
private static final double gkEpsilon = 0.000000000000000222;
	
private static final double eps_rel22 =       gkEpsilon * 1e4;
private static final double eps_tot22 =       gkEpsilon * 1e2;

private static double norm2(double[] a) { return (a[0] * a[0] + a[1] * a[1] + a[2] * a[2]); }
private static double dotProduct(double[] a, double[] b) { return (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]); }

private static void S3Dregion1234(double[] v, Simplex s) {
  v[0] = 0;
  v[1] = 0;
  v[2] = 0;
  s.nvrtx = 4;
}

private static void select_1ik(double[] si, double[] sk, Simplex s) {
  s.nvrtx = 3;
  for (int t = 0; t < 3; t++)
    s.vrtx[2][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = si[t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = sk[t];
}

private static void select_1ij(double[] si, double[] sj, Simplex s) {
  s.nvrtx = 3;
  for (int t = 0; t < 3; t++)
    s.vrtx[2][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = si[t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = sj[t];
}

private static void select_1jk(double[] sj, double[] sk, Simplex s) {
  s.nvrtx = 3;
  for (int t = 0; t < 3; t++)
    s.vrtx[2][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = sj[t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = sk[t];
}

private static void select_1i(double[] si, Simplex s) {
  s.nvrtx = 2;
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = si[t];
}

private static void select_1j(double[] sj, Simplex s) {
  s.nvrtx = 2;
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = sj[t];
}
  
private static void select_1k(double[] sk, Simplex s) {
  s.nvrtx = 2;
  for (int t = 0; t < 3; t++)
    s.vrtx[1][t] = s.vrtx[3][t];
  for (int t = 0; t < 3; t++)
    s.vrtx[0][t] = sk[t];
}

private static void getvrtx(double[] point, int location, Simplex s) {
  point[0] = s.vrtx[location][0];
  point[1] = s.vrtx[location][1];
  point[2] = s.vrtx[location][2];
}

private static void calculateEdgeVector(double[] p1p2, double[] p2, Simplex s) {
  p1p2[0] = p2[0] - s.vrtx[3][0];
  p1p2[1] = p2[1] - s.vrtx[3][1];
  p1p2[2] = p2[2] - s.vrtx[3][2];
}

private static void S1Dregion1(double[] v, Simplex s) {
  v[0] = s.vrtx[1][0];
  v[1] = s.vrtx[1][1];
  v[2] = s.vrtx[1][2];
  s.nvrtx = 1;
  s.vrtx[0][0] = s.vrtx[1][0];
  s.vrtx[0][1] = s.vrtx[1][1];
  s.vrtx[0][2] = s.vrtx[1][2];
}

private static void S2Dregion1(double[] v, Simplex s) {
  v[0] = s.vrtx[2][0];
  v[1] = s.vrtx[2][1];
  v[2] = s.vrtx[2][2];
  s.nvrtx = 1;
  s.vrtx[0][0] = s.vrtx[2][0];
  s.vrtx[0][1] = s.vrtx[2][1];
  s.vrtx[0][2] = s.vrtx[2][2];
}

private static void S2Dregion12(Simplex s) {
  s.nvrtx = 2;
  s.vrtx[0][0] = s.vrtx[2][0];
  s.vrtx[0][1] = s.vrtx[2][1];
  s.vrtx[0][2] = s.vrtx[2][2];
}

private static void S2Dregion13(Simplex s) {
  s.nvrtx = 2;
  s.vrtx[1][0] = s.vrtx[2][0];
  s.vrtx[1][1] = s.vrtx[2][1];
  s.vrtx[1][2] = s.vrtx[2][2];
}

private static void S3Dregion1(double[] v, double[] s1, Simplex s) {
  v[0] = s1[0];
  v[1] = s1[1];
  v[2] = s1[2];
  s.nvrtx = 1;
  s.vrtx[0][0] = s1[0];
  s.vrtx[0][1] = s1[1];
  s.vrtx[0][2] = s1[2];
}

private static double
determinant(double[] p, double[] q, double[] r) {
  return p[0] * ((q[1] * r[2]) - (r[1] * q[2])) - p[1] * (q[0] * r[2] - r[0] * q[2])
         + p[2] * (q[0] * r[1] - r[0] * q[1]);
}

private static void
crossProduct(double[] a, double[] b, double[] c) {
  c[0] = a[1] * b[2] - a[2] * b[1];
  c[1] = a[2] * b[0] - a[0] * b[2];
  c[2] = a[0] * b[1] - a[1] * b[0];
}

private static void
projectOnLine(double[] p, double[] q, double[] v) {
  double[] pq = new double[3];
  pq[0] = p[0] - q[0];
  pq[1] = p[1] - q[1];
  pq[2] = p[2] - q[2];

  double tmp = dotProduct(p, pq) / dotProduct(pq, pq);

  for (int i = 0; i < 3; i++) {
    v[i] = p[i] - pq[i] * tmp;
  }
}

private static void
projectOnPlane(double[] p, double[] q, double[] r, double[] v) {
  double[] n = new double[3];
  double[] pq = new double[3];
  double[] pr = new double[3];

  for (int i = 0; i < 3; i++) {
    pq[i] = p[i] - q[i];
  }
  for (int i = 0; i < 3; i++) {
    pr[i] = p[i] - r[i];
  }

  crossProduct(pq, pr, n);
  double tmp = dotProduct(n, p) / dotProduct(n, n);

  for (int i = 0; i < 3; i++) {
    v[i] = n[i] * tmp;
  }
}

private static boolean
hff1(double[] p, double[] q) {
  double tmp = 0;

  for (int i = 0; i < 3; i++) {
    tmp += (p[i] * p[i] - p[i] * q[i]);
  }

  return tmp > 0; // keep q
}

private static boolean
hff2(double[]p, double[]q, double[]r) {
  double[] ntmp = new double[3];
  double[] n = new double[3];
  double[] pq = new double[3];
  double[] pr = new double[3];

  for (int i = 0; i < 3; i++) {
    pq[i] = q[i] - p[i];
  }
  for (int i = 0; i < 3; i++) {
    pr[i] = r[i] - p[i];
  }

  crossProduct(pq, pr, ntmp);
  crossProduct(pq, ntmp, n);

  return dotProduct(p, n) < 0; // Discard r if true
}

private static boolean
hff3(double[]p, double[]q, double[]r) {
  double[] n = new double[3];
  double[] pq = new double[3];
  double[] pr = new double[3];

  for (int i = 0; i < 3; i++) {
    pq[i] = q[i] - p[i];
  }
  for (int i = 0; i < 3; i++) {
    pr[i] = r[i] - p[i];
  }

  crossProduct(pq, pr, n);
  return dotProduct(p, n) <= 0; // discard s if true
}

private static void
S1D(Simplex s, double[] v) {
  double[]s1p = s.vrtx[1];
  double[]s2p = s.vrtx[0];

  if (hff1(s1p, s2p)) {
    projectOnLine(s1p, s2p, v); // Update v, no need to update s
    return;                     // Return V{1,2}
  } else {
    S1Dregion1(v, s); // Update v and s
    return;       // Return V{1}
  }
}

private static void
S2D(Simplex s, double[] v) {
  double[] s1p = s.vrtx[2];
  double[] s2p = s.vrtx[1];
  double[] s3p = s.vrtx[0];
  boolean hff1f_s12 = hff1(s1p, s2p);
  boolean hff1f_s13 = hff1(s1p, s3p);

  if (hff1f_s12) {
    boolean hff2f_23 = !hff2(s1p, s2p, s3p);
    if (hff2f_23) {
      if (hff1f_s13) {
        boolean hff2f_32 = !hff2(s1p, s3p, s2p);
        if (hff2f_32) {
          projectOnPlane(s1p, s2p, s3p, v); // Update s, no need to update c
          return;                           // Return V{1,2,3}
        } else {
          projectOnLine(s1p, s3p, v); // Update v
          S2Dregion13(s);             // Update s
          return;                     // Return V{1,3}
        }
      } else {
        projectOnPlane(s1p, s2p, s3p, v); // Update s, no need to update c
        return;                           // Return V{1,2,3}
      }
    } else {
      projectOnLine(s1p, s2p, v); // Update v
      S2Dregion12(s);             // Update s
      return;                     // Return V{1,2}
    }
  } else if (hff1f_s13) {
    boolean hff2f_32 = !hff2(s1p, s3p, s2p);
    if (hff2f_32) {
      projectOnPlane(s1p, s2p, s3p, v); // Update s, no need to update v
      return;                           // Return V{1,2,3}
    } else {
      projectOnLine(s1p, s3p, v); // Update v
      S2Dregion13(s);             // Update s
      return;                     // Return V{1,3}
    }
  } else {
    S2Dregion1(v, s); // Update s and v
    return;           // Return V{1}
  }
}

private static void
S3D(Simplex s, double[] v) {
  double[] s1 = new double[3];
  double[] s2 = new double[3];
  double[] s3 = new double[3];
  double[] s4 = new double[3];
  double[] s1s2 = new double[3];
  double[] s1s3 = new double[3];
  double[] s1s4 = new double[3];
  double[] si = new double[3];
  double[] sj = new double[3];
  double[] sk = new double[3];
  int testLineThree, testLineFour, testPlaneTwo, testPlaneThree, testPlaneFour;
  int dotTotal;
  int i, j, k;

  getvrtx(s1, 3, s);
  getvrtx(s2, 2, s);
  getvrtx(s3, 1, s);
  getvrtx(s4, 0, s);
  calculateEdgeVector(s1s2, s2, s);
  calculateEdgeVector(s1s3, s3, s);
  calculateEdgeVector(s1s4, s4, s);

  int[] hff1_tests = new int[3];
  hff1_tests[2] = bool2int(hff1(s1, s2));
  hff1_tests[1] = bool2int(hff1(s1, s3));
  hff1_tests[0] = bool2int(hff1(s1, s4));
  testLineThree = bool2int(hff1(s1, s3));
  testLineFour = bool2int(hff1(s1, s4));

  dotTotal = bool2int(hff1(s1, s2)) + testLineThree + testLineFour;
  if (dotTotal == 0) { /* case 0.0 -------------------------------------- */
    S3Dregion1(v, s1, s);
    return;
  }

  double det134 = determinant(s1s3, s1s4, s1s2);
  int sss = bool2int(det134 <= 0);

  testPlaneTwo = bool2int(hff3(s1, s3, s4)) - sss;
  testPlaneTwo = testPlaneTwo * testPlaneTwo;
  testPlaneThree = bool2int(hff3(s1, s4, s2)) - sss;
  testPlaneThree = testPlaneThree * testPlaneThree;
  testPlaneFour = bool2int(hff3(s1, s2, s3)) - sss;
  testPlaneFour = testPlaneFour * testPlaneFour;

  switch (testPlaneTwo + testPlaneThree + testPlaneFour) {
    case 3:
      S3Dregion1234(v, s);
      break;

    case 2:
      // Only one facing the oring
      // 1,i,j, are the indices of the points on the triangle and remove k from
      // simplex
      s.nvrtx = 3;
      if (!int2bool(testPlaneTwo)) { // k = 2;   removes s2
        for (i = 0; i < 3; i++) {
          s.vrtx[2][i] = s.vrtx[3][i];
        }
      } else if (!int2bool(testPlaneThree)) { // k = 1; // removes s3
        for (i = 0; i < 3; i++) {
          s.vrtx[1][i] = s2[i];
        }
        for (i = 0; i < 3; i++) {
          s.vrtx[2][i] = s.vrtx[3][i];
        }
      } else if (!int2bool(testPlaneFour)) { // k = 0; // removes s4  and no need to reorder
        for (i = 0; i < 3; i++) {
          s.vrtx[0][i] = s3[i];
        }
        for (i = 0; i < 3; i++) {
          s.vrtx[1][i] = s2[i];
        }
        for (i = 0; i < 3; i++) {
          s.vrtx[2][i] = s.vrtx[3][i];
        }
      }
      // Call S2D
      S2D(s, v);
      break;
    case 1:
      // Two triangles face the origins:
      //    The only positive hff3 is for triangle 1,i,j, therefore k must be in
      //    the solution as it supports the the point of minimum norm.

      // 1,i,j, are the indices of the points on the triangle and remove k from
      // simplex
      s.nvrtx = 3;
      if (int2bool(testPlaneTwo)) {
        k = 2; // s2
        i = 1;
        j = 0;
      } else if (int2bool(testPlaneThree)) {
        k = 1; // s3
        i = 0;
        j = 2;
      } else {
        k = 0; // s4
        i = 2;
        j = 1;
      }

      getvrtx(si, i, s);
      getvrtx(sj, j, s);
      getvrtx(sk, k, s);

      if (dotTotal == 1) {
        if (int2bool(hff1_tests[k])) {
          if (!hff2(s1, sk, si)) {
            select_1ik(si, sk, s);
            projectOnPlane(s1, si, sk, v);
          } else if (!hff2(s1, sk, sj)) {
            select_1jk(sj, sk, s);
            projectOnPlane(s1, sj, sk, v);
          } else {
            select_1k(sk, s); // select region 1i
            projectOnLine(s1, sk, v);
          }
        } else if (int2bool(hff1_tests[i])) {
          if (!hff2(s1, si, sk)) {
            select_1ik(si, sk, s);
            projectOnPlane(s1, si, sk, v);
          } else {
            select_1i(si, s); // select region 1i
            projectOnLine(s1, si, v);
          }
        } else {
          if (!hff2(s1, sj, sk)) {
            select_1jk(sj, sk, s);
            projectOnPlane(s1, sj, sk, v);
          } else {
            select_1j(sj, s); // select region 1i
            projectOnLine(s1, sj, v);
          }
        }
      } else if (dotTotal == 2) {
        // Two edges have positive hff1, meaning that for two edges the origin's
        // project fall on the segement.
        //  Certainly the edge 1,k supports the the point of minimum norm, and so
        //  hff1_1k is positive

        if (int2bool(hff1_tests[i])) {
          if (!hff2(s1, sk, si)) {
            if (!hff2(s1, si, sk)) {
              select_1ik(si, sk, s); // select region 1ik
              projectOnPlane(s1, si, sk, v);
            } else {
              select_1k(sk, s); // select region 1k
              projectOnLine(s1, sk, v);
            }
          } else {
            if (!hff2(s1, sk, sj)) {
              select_1jk(sj, sk, s); // select region 1jk
              projectOnPlane(s1, sj, sk, v);
            } else {
              select_1k(sk, s); // select region 1k
              projectOnLine(s1, sk, v);
            }
          }
        } else if (int2bool(hff1_tests[j])) { //  there is no other choice
          if (!hff2(s1, sk, sj)) {
            if (!hff2(s1, sj, sk)) {
              select_1jk(sj, sk, s); // select region 1jk
              projectOnPlane(s1, sj, sk, v);
            } else {
              select_1j(sj, s); // select region 1j
              projectOnLine(s1, sj, v);
            }
          } else {
            if (!hff2(s1, sk, si)) {
              select_1ik(si, sk, s); // select region 1ik
              projectOnPlane(s1, si, sk, v);
            } else {
              select_1k(sk, s); // select region 1k
              projectOnLine(s1, sk, v);
            }
          }
        } else {
          // ERROR;
        }

      } else if (dotTotal == 3) {
        // MM : ALL THIS HYPHOTESIS IS FALSE
        // sk is s.t. hff3 for sk < 0. So, sk must support the origin because
        // there are 2 triangles facing the origin.

        boolean hff2_ik = hff2(s1, si, sk);
        boolean hff2_jk = hff2(s1, sj, sk);
        boolean hff2_ki = hff2(s1, sk, si);
        boolean hff2_kj = hff2(s1, sk, sj);

        if (!hff2_ki && !hff2_kj) {
//          throw new AssertionError("UNEXPECTED VALUES!!!");
        }
        if (hff2_ki && hff2_kj) {
          select_1k(sk, s);
          projectOnLine(s1, sk, v);
        } else if (hff2_ki) {
          // discard i
          if (hff2_jk) {
            // discard k
            select_1j(sj, s);
            projectOnLine(s1, sj, v);
          } else {
            select_1jk(sj, sk, s);
            projectOnPlane(s1, sk, sj, v);
          }
        } else {
          // discard j
          if (hff2_ik) {
            // discard k
            select_1i(si, s);
            projectOnLine(s1, si, v);
          } else {
            select_1ik(si, sk, s);
            projectOnPlane(s1, sk, si, v);
          }
        }
      }
      break;

    case 0:
      // The origin is outside all 3 triangles
      if (dotTotal == 1) {
        // Here si is set such that hff(s1,si) > 0
        if (int2bool(testLineThree)) {
          k = 2;
          i = 1; // s3
          j = 0;
        } else if (int2bool(testLineFour)) {
          k = 1; // s3
          i = 0;
          j = 2;
        } else {
          k = 0;
          i = 2; // s2
          j = 1;
        }
        getvrtx(si, i, s);
        getvrtx(sj, j, s);
        getvrtx(sk, k, s);

        if (!hff2(s1, si, sj)) {
          select_1ij(si, sj, s);
          projectOnPlane(s1, si, sj, v);
        } else if (!hff2(s1, si, sk)) {
          select_1ik(si, sk, s);
          projectOnPlane(s1, si, sk, v);
        } else {
          select_1i(si, s);
          projectOnLine(s1, si, v);
        }
      } else if (dotTotal == 2) {
        // Here si is set such that hff(s1,si) < 0
        s.nvrtx = 3;
        if (!int2bool(testLineThree)) {
          k = 2;
          i = 1; // s3
          j = 0;
        } else if (!int2bool(testLineFour)) {
          k = 1;
          i = 0; // s4
          j = 2;
        } else {
          k = 0;
          i = 2; // s2
          j = 1;
        }
        getvrtx(si, i, s);
        getvrtx(sj, j, s);
        getvrtx(sk, k, s);

        if (!hff2(s1, sj, sk)) {
          if (!hff2(s1, sk, sj)) {
            select_1jk(sj, sk, s); // select region 1jk
            projectOnPlane(s1, sj, sk, v);
          } else if (!hff2(s1, sk, si)) {
            select_1ik(si, sk, s);
            projectOnPlane(s1, sk, si, v);
          } else {
            select_1k(sk, s);
            projectOnLine(s1, sk, v);
          }
        } else if (!hff2(s1, sj, si)) {
          select_1ij(si, sj, s);
          projectOnPlane(s1, si, sj, v);
        } else {
          select_1j(sj, s);
          projectOnLine(s1, sj, v);
        }
      }
      break;
    default:
      throw new AssertionError("ERROR: unhandled");
  }
}

private static void
support(Polytope body, double[]v) {
  double s, maxs;
  double[] vrt;
  int better = -1;

  maxs = dotProduct(body.s, v);

  for (int i = 0; i < body.coord.length; ++i) {
    vrt = body.coord[i];
    s = dotProduct(vrt, v);
    if (s > maxs) {
      maxs = s;
      better = i;
    }
  }

  if (better != -1) {
    body.s[0] = body.coord[better][0];
    body.s[1] = body.coord[better][1];
    body.s[2] = body.coord[better][2];
  }
}

private static void
subalgorithm(Simplex s, double[] v) {
  switch (s.nvrtx) {
    case 4:
      S3D(s, v);
      break;
    case 3:
      S2D(s, v);
      break;
    case 2:
      S1D(s, v);
      break;
    default:
      throw new AssertionError("ERROR: invalid simplex");
  }
}

public static double
compute_minimum_distance(Polytope bd1, Polytope bd2, Simplex s) {
  int k = 0;                /**< Iteration counter                 */
  int mk = 25;                 /**< Maximum number of GJK iterations  */
  double eps_rel = eps_rel22; /**< Tolerance on relative             */
  double eps_tot = eps_tot22; /**< Tolerance on absolute distance    */

  double eps_rel2 = eps_rel * eps_rel;
  int i;
  double[] w = new double[3];
  double[] v = new double[3];
  double[] vminus = new double[3];
  double norm2Wmax = 0;

  /* Initialise search direction */
  v[0] = bd1.coord[0][0] - bd2.coord[0][0];
  v[1] = bd1.coord[0][1] - bd2.coord[0][1];
  v[2] = bd1.coord[0][2] - bd2.coord[0][2];

  /* Inialise simplex */
  s.nvrtx = 1;
  for (int t = 0; t < 3; ++t) {
    s.vrtx[0][t] = v[t];
  }

  for (int t = 0; t < 3; ++t) {
    bd1.s[t] = bd1.coord[0][t];
  }

  for (int t = 0; t < 3; ++t) {
    bd2.s[t] = bd2.coord[0][t];
  }

  /* Begin GJK iteration */
  do {
    k++;

    /* Update negative search direction */
    for (int t = 0; t < 3; ++t) {
      vminus[t] = -v[t];
    }

    /* Support function */
    support(bd1, vminus);
    support(bd2, v);
    for (int t = 0; t < 3; ++t) {
      w[t] = bd1.s[t] - bd2.s[t];
    }

    /* Test first exit condition (new point already in simplex/can't move
     * further) */
    double exeedtol_rel = (norm2(v) - dotProduct(v, w));
    if (exeedtol_rel <= (eps_rel * norm2(v)) || exeedtol_rel < eps_tot22) {
      break;
    }

    if (norm2(v) < eps_rel2) { // it a null V
      break;
    }

    /* Add new vertex to simplex */
    i = s.nvrtx;
    for (int t = 0; t < 3; ++t) {
      s.vrtx[i][t] = w[t];
    }
    s.nvrtx++;

    /* Invoke distance sub-algorithm */
    subalgorithm(s, v);

    /* Test */
    for (int jj = 0; jj < s.nvrtx; jj++) {
      double tesnorm = norm2(s.vrtx[jj]);
      if (tesnorm > norm2Wmax) {
        norm2Wmax = tesnorm;
      }
    }

    if ((norm2(v) <= (eps_tot * eps_tot * norm2Wmax))) {
      break;
    }

  } while ((s.nvrtx != 4) && (k != mk));

  if (k == mk) {
//    mexPrintf("\n * * * * * * * * * * * * MAXIMUM ITERATION NUMBER REACHED!!!  "
//              " * * * * * * * * * * * * * * \n");
  }

  return Math.sqrt(norm2(v));
}


}

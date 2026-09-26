// frontend/lib/widgets/endorsement/mini_graph_preview.dart

import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:shared/shared.dart' hide AppColors;
import '../../constants/app_colors.dart';

/// A static, non-interactive mini graph preview for the Trust Network card.
///
/// Renders a compact 1-hop neighbourhood:
///   - One larger centre node (the current user)
///   - Several smaller outer nodes (endorsers)
///   - Thin edges from centre to outer nodes
///   - A few cross-connections between outer nodes to suggest clustering
///
/// Node positions are precomputed deterministically so the same user
/// always renders the same layout.
class MiniGraphPreview extends StatelessWidget {
  final List<EndorsementGraphNode> nodes;
  final List<EndorsementGraphEdge> edges;
  final double width;
  final double height;

  const MiniGraphPreview({
    super.key,
    required this.nodes,
    required this.edges,
    this.width = 200,
    this.height = 60,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: width,
      height: height,
      child: CustomPaint(
        painter: _MiniGraphPainter(
          nodes: nodes,
          edges: edges,
          tealColor: AppColors.primaryTeal(context),
          charcoalColor: AppColors.charcoal(context),
          yellowColor: AppColors.citrusYellow(context),
          orangeColor: AppColors.error(context),
        ),
      ),
    );
  }
}

class _MiniGraphPainter extends CustomPainter {
  final List<EndorsementGraphNode> nodes;
  final List<EndorsementGraphEdge> edges;
  final Color tealColor;
  final Color charcoalColor;
  final Color yellowColor;
  final Color orangeColor;

  _MiniGraphPainter({
    required this.nodes,
    required this.edges,
    required this.tealColor,
    required this.charcoalColor,
    required this.yellowColor,
    required this.orangeColor,
  });

  /// Deterministic position for each node based on its index.
  /// The centre node sits at the middle; others are placed on a
  /// precomputed ring with slight jitter to feel organic.
  Map<String, Offset> _computePositions(Size size) {
    final positions = <String, Offset>{};

    final centre = Offset(size.width / 2, size.height / 2);
    final outerNodes = <EndorsementGraphNode>[];

    for (final node in nodes) {
      if (node.isCentreNode) {
        positions[node.userId] = centre;
      } else {
        outerNodes.add(node);
      }
    }

    // Radius scales with canvas — leaves padding for node size
    final radiusX = (size.width / 2) - 16;
    final radiusY = (size.height / 2) - 12;

    for (var i = 0; i < outerNodes.length; i++) {
      // Distribute around the ring with jitter
      final angle = (2 * math.pi * i) / outerNodes.length;
      final jitterX = (i % 3 - 1) * 4.0;
      final jitterY = (i % 2 == 0 ? 1 : -1) * 3.0;

      positions[outerNodes[i].userId] = Offset(
        centre.dx + math.cos(angle) * radiusX + jitterX,
        centre.dy + math.sin(angle) * radiusY + jitterY,
      );
    }

    return positions;
  }

  Color _edgeColor(String skillTag) {
    switch (skillTag) {
      case 'reliable':
      case 'trustworthy':
        return tealColor;
      case 'communicative':
      case 'punctual':
        return yellowColor;
      case 'pet_care':
      case 'gardening':
        return orangeColor;
      default:
        return charcoalColor.withValues(alpha: 0.4);
    }
  }

  @override
  void paint(Canvas canvas, Size size) {
    if (nodes.isEmpty) return;

    final positions = _computePositions(size);

    // Draw edges first (so nodes render on top)
    for (final edge in edges) {
      final from = positions[edge.fromUserId];
      final to = positions[edge.toUserId];
      if (from == null || to == null) continue;

      final paint = Paint()
        ..color = _edgeColor(edge.skillTag).withValues(alpha: 0.5)
        ..strokeWidth = edge.weight >= 3 ? 1.6 : 1.0
        ..style = PaintingStyle.stroke
        ..strokeCap = StrokeCap.round;

      canvas.drawLine(from, to, paint);
    }

    // Draw nodes
    for (final node in nodes) {
      final pos = positions[node.userId];
      if (pos == null) continue;

      final isCentre = node.isCentreNode;
      final radius = isCentre ? 8.0 : 4.0;

      // Node fill
      final fillPaint = Paint()
        ..color = isCentre ? tealColor : charcoalColor.withValues(alpha: 0.6)
        ..style = PaintingStyle.fill;

      canvas.drawCircle(pos, radius, fillPaint);

      // Centre node gets a soft halo
      if (isCentre) {
        final haloPaint = Paint()
          ..color = tealColor.withValues(alpha: 0.15)
          ..style = PaintingStyle.fill;
        canvas.drawCircle(pos, radius + 4, haloPaint);
      }
    }
  }

  @override
  bool shouldRepaint(covariant _MiniGraphPainter oldDelegate) {
    return oldDelegate.nodes != nodes || oldDelegate.edges != edges;
  }
}